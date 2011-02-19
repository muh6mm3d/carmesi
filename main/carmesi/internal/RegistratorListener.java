package carmesi.internal;

import carmesi.jsonserializers.JSONSerializer;
import carmesi.internal.dynamic.DynamicController;
import carmesi.BeforeURL;
import carmesi.Controller;
import carmesi.URL;
import carmesi.convertion.Converter;
import carmesi.convertion.ConverterFor;
import carmesi.internal.dynamic.DynamicControllerServlet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
 * Register the CarmesiFilter and CarmesiServlet with the controllers specified in the config file.
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
public class RegistratorListener implements ServletContextListener {

    public static final String CONFIG_FILE_PATH = "META-INF/carmesi.list";
    private ServletContext context;
    
    private ControllerFactory controllerFactory;
    private Map<Class, Converter> converterMap=new HashMap<Class, Converter>();

    public void contextInitialized(ServletContextEvent sce) {
        context = sce.getServletContext();
        try{
            new InitialContext().lookup("java:comp/BeanManager");
            setControllerFactory(new BeanManagerControllerFactory());
        }catch(NamingException ex){
            setControllerFactory(new SimpleControllerFactory());
        }
        controllerFactory.init();
        try {
            addClassesFromConfigFile();
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

    protected void setControllerFactory(ControllerFactory controllerFactory) {
        this.controllerFactory = controllerFactory;
    }
    
    public void contextDestroyed(ServletContextEvent sce) {
        controllerFactory.dispose();
    }
    
    private void addClassesFromConfigFile() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        InputStream input = getClass().getResourceAsStream("/" + CONFIG_FILE_PATH);
        if(input != null){
            addClasses(input);
        }
    }

    private void addClasses(InputStream input) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line;
        Set<Class> controllersURL=new HashSet<Class>();
        Set<Class> controllersBeforeURL=new HashSet<Class>();
        while ((line = reader.readLine()) != null) {
            if (line.trim().startsWith("#")) {
                continue;
            }
            Class klass = Class.forName(line);
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
        for(Class klass:controllersURL){
            addControllerServlet((Class<Object>) klass);    
        }
        for(Class klass:controllersBeforeURL){
            addControllerFilter((Class<Object>) klass);
        }
    }
    
    private String getParameter(String name, String defaultValue){
        if(context.getInitParameter(name) != null){
            return context.getInitParameter(name);
        }else{
            return defaultValue;
        }
    }
    
    private void addControllerServlet(Class<Object> klass) throws InstantiationException, IllegalAccessException {
        AbstractControllerServlet servlet;
        if (Controller.class.isAssignableFrom(klass)) {
            servlet=new TypeSafeControllerServlet(controllerFactory.createController(klass.asSubclass(Controller.class)));
        }else{
            DynamicController dynamicController=DynamicController.createDynamicController(controllerFactory.createController(klass));
            configureDynamicController(dynamicController);
            servlet=new DynamicControllerServlet(dynamicController);
        }
        ServletRegistration.Dynamic dynamic = context.addServlet(klass.getSimpleName(), servlet);
        URL url = klass.getAnnotation(URL.class);
        dynamic.addMapping(url.value());
    }
    
    private void addControllerFilter(Class<Object> klass) throws InstantiationException, IllegalAccessException {
        ControllerFilter filter;
        if (Controller.class.isAssignableFrom(klass)) {
            filter=new ControllerFilter(controllerFactory.createController(klass.asSubclass(Controller.class)));
        }else{
            DynamicController dynamicController = DynamicController.createDynamicController(controllerFactory.createController(klass));
            configureDynamicController(dynamicController);
            filter=new ControllerFilter(dynamicController);
        }
        FilterRegistration.Dynamic dynamic = context.addFilter(klass.getSimpleName(), filter);
        BeforeURL before = klass.getAnnotation(BeforeURL.class);
        EnumSet<DispatcherType> set = EnumSet.of(DispatcherType.REQUEST);
        dynamic.addMappingForUrlPatterns(set, false, before.value());
    }
    
    private void configureDynamicController(DynamicController controller){
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
            Logger.getLogger(RegistratorListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(RegistratorListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(RegistratorListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void addConverter(Class<? extends Converter> klass) throws InstantiationException, IllegalAccessException{
        Converter converter=klass.newInstance();
        converterMap.put(klass.getAnnotation(ConverterFor.class).value(), converter);
    }

}
