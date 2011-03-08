/* Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0) */

package carmesi.internal;

import carmesi.jsonserializers.JSONSerializer;
import carmesi.internal.simplecontrollers.SimpleControllerWrapper;
import carmesi.BeforeURL;
import carmesi.Controller;
import carmesi.ForwardTo;
import carmesi.HttpMethod;
import carmesi.RedirectTo;
import carmesi.URL;
import carmesi.convert.Converter;
import carmesi.convert.ConverterFor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebListener;

/**
 *
 * ServletContextListener implementation to add the servlets and filter for executing the controllers specified in config resources.
 * <p>
 * The config file is a simple text containing a list of full class name of the controllers. Each class name is specified in a separate line.
 * If a line is empty or starts with a '#' symbol is skipped.
 * <p>
 * The name of config file must be controller.list within META-INF directory in the directory of classes of the web project (that is, WEB-INF/classes).
 * <p>
 * Carmesi includes an annotation processor for generating automatically this file without the user intervention.
 * 
 * TODO check if this is a compatible CDI Extension (or make).
 * 
 * @author Victor Hugo Herrera Maldonado
 */
@WebListener
public class CarmesiInitializer implements ServletContextListener {

    public static final String CONFIG_FILE_PATH = "META-INF/carmesi.list";
    private ServletContext context;
    
    private ObjectFactory controllerFactory;
    private Map<Class, Converter> converterMap=new HashMap<Class, Converter>();
    private Set<java.net.URL> configResources=new HashSet<java.net.URL>();

    CarmesiInitializer(ObjectFactory factory, java.net.URL... configResources) {
        assert factory != null;
        assert configResources != null;
        setControllerFactory(factory);
        this.configResources.addAll(Arrays.asList(configResources));
    }

    public CarmesiInitializer() {
        
    }
    
    public final void contextInitialized(ServletContextEvent sce) {
        assert sce != null;
        context = sce.getServletContext();
        if(controllerFactory == null){
            try{
                new InitialContext().lookup("java:comp/BeanManager");
                setControllerFactory(new CDIObjectFactory());
            }catch(NamingException ex){
                setControllerFactory(new SimpleObjectFactory());
            }
        }
        controllerFactory.init();
        java.net.URL defaultConfigResource = getClass().getResource("/" + CONFIG_FILE_PATH);
        if(defaultConfigResource != null){
            configResources.add(defaultConfigResource);
        }
        try {
            addClassesFromConfigResources();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        } catch (InstantiationException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void setControllerFactory(ObjectFactory controllerFactory) {
        assert controllerFactory != null;
        this.controllerFactory = controllerFactory;
    }
    
    public final void contextDestroyed(ServletContextEvent sce) {
        assert sce != null;
        controllerFactory.dispose();
    }
    
    private void addClassesFromConfigResources() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        Set<Class> controllersURL=new HashSet<Class>();
        Set<Class> controllersBeforeURL=new HashSet<Class>();
        for(java.net.URL url:configResources){
            InputStream input=url.openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().startsWith("#")) {
                    continue;
                }
                Class klass = Class.forName(getBinaryClassname(line));
                if (klass.isAnnotationPresent(URL.class)) {
                    controllersURL.add(klass);
                } else if (klass.isAnnotationPresent(BeforeURL.class)){
                    controllersBeforeURL.add(klass);
                } else if (klass.isAnnotationPresent(ConverterFor.class)){
                    if(Converter.class.isAssignableFrom(klass)){
                        addConverter((Class<? extends Converter>) klass);
                    }
                }
            }
            reader.close();
            
        }
        for(Class klass:controllersURL){
            addControllerServlet((Class<Object>) klass);    
        }
        for(Class klass:controllersBeforeURL){
            addControllerFilter((Class<Object>) klass);
        }
    }

    private String getParameter(String name, String defaultValue){
        assert name != null;
        assert defaultValue != null;
        if(context.getInitParameter(name) != null){
            return context.getInitParameter(name);
        }else{
            return defaultValue;
        }
    }
    
    private void addControllerServlet(Class<Object> klass) throws InstantiationException, IllegalAccessException {
        assert klass != null;
        ControllerServlet servlet=null;
        Controller controller;
        if (Controller.class.isAssignableFrom(klass)) {
            controller = controllerFactory.createController(klass.asSubclass(Controller.class));
        }else{
            SimpleControllerWrapper simpleController=SimpleControllerWrapper.createInstance(controllerFactory.createController(klass));
            configure(simpleController);
            controller=simpleController;
        }
        URL url=klass.getAnnotation(URL.class);
        HttpMethod[] validHttpMethods= url != null ? HttpMethod.values() : (url.httpMethods().length == 0 ? HttpMethod.values(): url.httpMethods());
        servlet=new ControllerServlet(controller);
        servlet.setValidHttpMethods(validHttpMethods);
        ControllerServlet.AfterControllerAction action=null;
        if(klass.isAnnotationPresent(ForwardTo.class)){
            action=new ControllerServlet.ForwardAction(klass.getAnnotation(ForwardTo.class).value());
        }else if(klass.isAnnotationPresent(RedirectTo.class)){
            action=new ControllerServlet.ForwardAction(klass.getAnnotation(RedirectTo.class).value());
        }
        servlet.setAfterControllerAction(action);
        ServletRegistration.Dynamic dynamic = context.addServlet(klass.getSimpleName(), servlet);
        dynamic.addMapping(url.value());
    }
    
    private void addControllerFilter(Class<Object> klass) throws InstantiationException, IllegalAccessException {
        assert klass != null;
        ControllerFilter filter;
        if (Controller.class.isAssignableFrom(klass)) {
            filter=new ControllerFilter(controllerFactory.createController(klass.asSubclass(Controller.class)));
        }else{
            SimpleControllerWrapper simpleController = SimpleControllerWrapper.createInstance(controllerFactory.createController(klass));
            configure(simpleController);
            filter=new ControllerFilter(simpleController);
        }
        FilterRegistration.Dynamic dynamic = context.addFilter(klass.getSimpleName(), filter);
        BeforeURL before = klass.getAnnotation(BeforeURL.class);
        EnumSet<DispatcherType> set = EnumSet.of(DispatcherType.REQUEST);
        dynamic.addMappingForUrlPatterns(set, false, before.value());
    }
    
    private void configure(SimpleControllerWrapper controller){
        assert controller != null;
        try {
            controller.setAutoRequestAttribute(Boolean.parseBoolean(getParameter("carmesi.requestAttribute.autoGeneration", "true")));
            controller.setDefaultCookieMaxAge(Integer.parseInt(getParameter("carmesi.cookie.maxAge", "-1")));
            String jsonSerializerClassname=getParameter("carmesi.json.serializer", "carmesi.jsonserializers.JacksonSerializer");
            Class<?> serializerKlass = Class.forName(jsonSerializerClassname);
            if(JSONSerializer.class.isAssignableFrom(serializerKlass)){
                controller.setJSONSerializer(serializerKlass.asSubclass(JSONSerializer.class).newInstance());
            }
            for(Map.Entry<Class, Converter> entry:converterMap.entrySet()){
                controller.addConverter(entry.getKey(), entry.getValue());
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CarmesiInitializer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(CarmesiInitializer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(CarmesiInitializer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void addConverter(Class<? extends Converter> klass) throws InstantiationException, IllegalAccessException{
        assert klass != null;
        Converter converter=klass.newInstance();
        converterMap.put(klass.getAnnotation(ConverterFor.class).value(), converter);
    }

    private String getBinaryClassname(String canonicalClassname) {
        assert canonicalClassname != null;
        String[] parts=canonicalClassname.split("\\.");
        boolean couldBeNestedClass=false;
        StringBuilder builder=new StringBuilder();
        for(String part:parts){
            if(builder.length() > 0){
                builder.append(couldBeNestedClass? '$': '.');
            }
            builder.append(part);
            if(!couldBeNestedClass && Character.isUpperCase(part.charAt(0))){
                couldBeNestedClass=true;
            }
        }
        return builder.toString();
    }

}

