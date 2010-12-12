/**
 * Insert license here.
 */

package carmesi.internal;

import carmesi.Before;
import carmesi.Controller;
import carmesi.ObjectProducer;
import carmesi.URL;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.EnumSet;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
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
    public static final String CONFIG_FILE_PATH="META-INF/controllers.list";

    private ServletContext context;
    private CarmesiServlet carmesiServlet;
    private Dynamic dymanicServlet;
    private CarmesiFilter carmesiFilter;

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

    private void addClassesFromConfigFile() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException{
        InputStream input;// = context.getResourceAsStream("META-INF/controllers.list");
        input=getClass().getResourceAsStream("/"+CONFIG_FILE_PATH);
//        System.out.println("input: "+input);
        if (input != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String line;
            while ((line = reader.readLine()) != null) {
//                System.out.println("line: " + line);
                if (line.trim().startsWith("#")) {
                    continue;
                }
                Class<?> klass = Class.forName(line);
                if(Controller.class.isAssignableFrom(klass)){
                    Class<? extends Controller> subclass = klass.asSubclass(Controller.class);
                    addControllerClass(subclass);
                }
                if(ObjectProducer.class.isAssignableFrom(klass)){
                    Class<? extends ObjectProducer> subclass = klass.asSubclass(ObjectProducer.class);
                    addObjectProducerClass(dymanicServlet, subclass);
                }
            }
            reader.close();
        }
    }

    private void addObjectProducerClass(Dynamic dynamic, Class<? extends ObjectProducer> klass) throws InstantiationException, IllegalAccessException {
        URL url = klass.getAnnotation(URL.class);
//        System.out.println("info: " + url);
        carmesiServlet.addObjectProducer(url.value(), klass.newInstance());
        dynamic.addMapping(url.value());
    }

    private void addControllerClass(Class<? extends Controller> klass) throws InstantiationException, IllegalAccessException {
        System.out.println("controller class: "+klass);
        if (klass.isAnnotationPresent(Before.class)) {
            addControllerBeforeRequest(klass);
        } else if (klass.isAnnotationPresent(URL.class)){
            addControllerToView(dymanicServlet, klass);
        }
    }

    private void addControllerToView(Dynamic dynamic, Class<? extends Controller> klass) throws IllegalAccessException, InstantiationException {
        System.out.println(klass);
        URL url = klass.getAnnotation(URL.class);
        System.out.println("info: " + url);
        carmesiServlet.addController(url.value(), klass.newInstance());
        dynamic.addMapping(url.value());
    }

    private void addControllerBeforeRequest(Class<? extends Controller> klass) throws InstantiationException, IllegalAccessException {
        Before beforeRequest = klass.getAnnotation(Before.class);
        carmesiFilter.addController(beforeRequest.value(), klass.newInstance());
    }

    public void contextDestroyed(ServletContextEvent sce) {
    }
    
}
