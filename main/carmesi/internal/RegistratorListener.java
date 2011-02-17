/**
 * Insert license here.
 */
package carmesi.internal;

import carmesi.internal.dynamic.DynamicController;
import carmesi.BeforeURL;
import carmesi.Controller;
import carmesi.URL;
import carmesi.internal.dynamic.DynamicControllerServlet;
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

    public static final String CONFIG_FILE_PATH = "META-INF/controllers.list";
    private ServletContext context;
    
    private @Inject BeanManager beanManager;
    private CreationalContext creationalContext;
    private Collection objects=new LinkedList();

    public void contextInitialized(ServletContextEvent sce) {
        try {
            context = sce.getServletContext();
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
                if (klass.isAnnotationPresent(URL.class)) {
                    addControllerServlet((Class<Object>) klass);
                } else if (klass.isAnnotationPresent(BeforeURL.class)){
                    addControllerFilter((Class<Object>) klass);
                }
            }
            reader.close();
        }
    }
    
    private void addControllerServlet(Class<Object> klass) throws InstantiationException, IllegalAccessException {
        AbstractControllerServlet servlet;
        if (Controller.class.isAssignableFrom(klass)) {
            servlet=new TypeSafeControllerServlet(createObject(klass.asSubclass(Controller.class)));
        }else{
            servlet=new DynamicControllerServlet(createObject(klass));
        }
        ServletRegistration.Dynamic dynamic = context.addServlet(klass.getSimpleName(), servlet);
        URL url = klass.getAnnotation(URL.class);
        dynamic.addMapping(url.value());
    }

    private void addControllerFilter(Class<Object> klass) throws InstantiationException, IllegalAccessException {
        ControllerFilter filter;
        if (Controller.class.isAssignableFrom(klass)) {
            filter=new ControllerFilter(createObject(klass.asSubclass(Controller.class)));
        }else{
            filter=new ControllerFilter(DynamicController.createDynamicController(createObject(klass)));
        }
        FilterRegistration.Dynamic dynamic = context.addFilter(klass.getSimpleName(), filter);
        BeforeURL before = klass.getAnnotation(BeforeURL.class);
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
