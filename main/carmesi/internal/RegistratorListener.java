/**
 * Insert license here.
 */
package carmesi.internal;

import carmesi.Before;
import carmesi.Controller;
import carmesi.URL;
import carmesi.umbrella.DynamicControllerServlet;
import carmesi.umbrella.DynamicControllerFilter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedList;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.inject.Inject;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRegistration.Dynamic;
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
 * Carmesi includes an annotation processor for generating automatically this file without the user intervantion.
 * 
 * @author Victor Hugo Herrera Maldonado
 */
@WebListener
public class RegistratorListener implements ServletContextListener {

    public static final String CONFIG_FILE_PATH = "META-INF/controllers.list";
    private ServletContext context;
    private CarmesiServlet carmesiServlet;
    private Dynamic dymanicServlet;
    private CarmesiFilter carmesiFilter;
    
    private @Inject BeanManager beanManager;
    private CreationalContext creationalContext;
    private Collection objects=new LinkedList();

    public void contextInitialized(ServletContextEvent sce) {
        try {
            //Controller with view
            context = sce.getServletContext();
            carmesiServlet = new CarmesiServlet();
            dymanicServlet = context.addServlet("Umbrella Servlet", carmesiServlet);

            //Controller before page
            carmesiFilter = new CarmesiFilter();
            FilterRegistration.Dynamic dynamicFilter = sce.getServletContext().addFilter("Umbrella Filter", carmesiFilter);
            EnumSet<DispatcherType> set = EnumSet.of(DispatcherType.REQUEST);
            dynamicFilter.addMappingForUrlPatterns(set, false, "/*");

            addClassesFromConfigFile();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void contextDestroyed(ServletContextEvent sce) {
        disposeObjects();
    }

    private void addClassesFromConfigFile() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        InputStream input;
        input = getClass().getResourceAsStream("/" + CONFIG_FILE_PATH);
        if (input != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().startsWith("#")) {
                    continue;
                }
                Class<?> klass = Class.forName(line);
                if (Controller.class.isAssignableFrom(klass)) {
                    Class<? extends Controller> subclass = klass.asSubclass(Controller.class);
                    addControllerClass(subclass);
                }
                /*if(ObjectProducer.class.isAssignableFrom(klass)){
                Class<? extends ObjectProducer> subclass = klass.asSubclass(ObjectProducer.class);
                addObjectProducerClass(dymanicServlet, subclass);
                }*/

                if (!Controller.class.isAssignableFrom(klass) /* && !ObjectProducer.class.isAssignableFrom(klass)*/ ) {
                    URL url = klass.getAnnotation(URL.class);
                    if (url != null) {
                        addDynamicControllerServlet((Class<Object>) klass);
                    } else {
                        Before before = klass.getAnnotation(Before.class);
                        if (before != null) {
                            addDynamicControllerFilter((Class<Object>) klass);
                        }
                    }
                }

            }
            reader.close();
        }
    }
    
    private void addControllerClass(Class<? extends Controller> klass) throws InstantiationException, IllegalAccessException {
        System.out.println("controller class: " + klass);
        if (klass.isAnnotationPresent(URL.class)) {
            addControllerToView(dymanicServlet, klass);
        }else if (klass.isAnnotationPresent(Before.class)) {
            addControllerBeforeRequest(klass);
        } 
    }

    private void addControllerToView(Dynamic dynamic, Class<? extends Controller> klass) throws IllegalAccessException, InstantiationException {
        URL url = klass.getAnnotation(URL.class);
        carmesiServlet.addController(url.value(), createObject(klass));
        dynamic.addMapping(url.value());
    }

    private void addControllerBeforeRequest(Class<? extends Controller> klass) throws InstantiationException, IllegalAccessException {
        Before beforeRequest = klass.getAnnotation(Before.class);
        carmesiFilter.addController(beforeRequest.value(), createObject(klass));
    }

    private void addDynamicControllerServlet(Class<Object> klass) throws InstantiationException, IllegalAccessException {
        Object object = createObject(klass);
        ServletRegistration.Dynamic dynamic = context.addServlet(klass.getSimpleName(), new DynamicControllerServlet(object));
        URL url = klass.getAnnotation(URL.class);
        dynamic.addMapping(url.value());
    }

    private void addDynamicControllerFilter(Class<Object> klass) throws InstantiationException, IllegalAccessException {
        Object object = createObject(klass);
        FilterRegistration.Dynamic dynamic = context.addFilter(klass.getSimpleName(), new DynamicControllerFilter(object));
        Before before = klass.getAnnotation(Before.class);
        EnumSet<DispatcherType> set = EnumSet.of(DispatcherType.REQUEST);
        dynamic.addMappingForUrlPatterns(set, false, before.value());
    }

    public <T extends Object> T createObject(Class<T> klass) throws InstantiationException, IllegalAccessException {
        T object;
        if(beanManager != null){
            AnnotatedType<T> annotatedType = beanManager.createAnnotatedType(klass);
            InjectionTarget<T> target = beanManager.createInjectionTarget(annotatedType);
            if(creationalContext == null){
                creationalContext = beanManager.createCreationalContext(null);
            }
            object = target.produce(creationalContext);
            target.inject(object, creationalContext);
            target.postConstruct(object);
            objects.add(target);
        }else{
            object=klass.newInstance();
            objects.add(object);
        }
        return object;
    }

    private void disposeObjects() {
        for(Object o:objects){
            if(o instanceof InjectionTarget){
                InjectionTarget target=(InjectionTarget) o;
                target.preDestroy(o);
                target.dispose(o);
            }
        }
        if(creationalContext != null){
            creationalContext.release();
        }
    }
    
}
