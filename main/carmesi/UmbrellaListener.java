/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package umbrella;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration.Dynamic;
import javax.servlet.annotation.WebListener;
//import org.scannotation.AnnotationDB;
//import org.scannotation.WarUrlFinder;

/**
 *
 * @author Victor
 */
@WebListener
public class UmbrellaListener implements ServletContextListener{
    private ServletContext context;
    private UmbrellaServlet umbrellaServlet;
    private Dynamic dymanicServlet;
    private UmbrellaFilter umbrellaFilter;

    public void contextInitialized(ServletContextEvent sce) {
        try{
            //Controller with view
            context=sce.getServletContext();
            umbrellaServlet = new UmbrellaServlet();
            dymanicServlet = context.addServlet("Umbrella Servlet", umbrellaServlet);

            //Controller before page
            umbrellaFilter=new UmbrellaFilter();
            FilterRegistration.Dynamic dynamicFilter = sce.getServletContext().addFilter("Umbrella Filter", umbrellaFilter);
            EnumSet<DispatcherType> set=EnumSet.of(DispatcherType.REQUEST);
            dynamicFilter.addMappingForUrlPatterns(set, false, "/*");


//            AnnotationDB annotationDB = new AnnotationDB();
//            annotationDB.scanArchives(WarUrlFinder.findWebInfClassesPath(context));
//            Map<String, Set<String>> mapAnnotatedClasses = annotationDB.getAnnotationIndex();
//            if(mapAnnotatedClasses.containsKey(BeforeView.class.getName())){
//                for (String classname : mapAnnotatedClasses.get(BeforeView.class.getName())) {
//                    Class<?> klass = Class.forName(classname);
//                    try{
//                        Class<? extends Controller> subclass = klass.asSubclass(Controller.class);
//                        addControllerClass(subclass);
//                    }catch(ClassCastException ex){
//
//                    }
//                }
//            }
//            if(mapAnnotatedClasses.containsKey(RequestReceiver.class.getName())){
//                for (String classname : mapAnnotatedClasses.get(RequestReceiver.class.getName())) {
//                    Class<?> klass = Class.forName(classname);
//                    try{
//                        Class<? extends Controller> subclass = klass.asSubclass(Controller.class);
//                        addControllerClass(subclass);
//                    }catch(ClassCastException ex){
//
//                    }
//                }
//            }

//            System.out.println("map: "+mapAnnotatedClasses);
            InputStream input = context.getResourceAsStream("/WEB-INF/controllers.list");
            System.out.println("input: "+input);
            if(input != null){
                BufferedReader reader=new BufferedReader(new InputStreamReader(input));
                String line;
                while((line=reader.readLine()) != null){
                    System.out.println("line: "+ line);
                    if(line.trim().startsWith("#")){
                        continue;
                    }
                    Class<?> klass = Class.forName(line);
                    try{
                        Class<? extends Controller> subclass = klass.asSubclass(Controller.class);
                        addControllerClass(subclass);
                    }catch(ClassCastException ex){

                    }
                }
                reader.close();
            }
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    private void addControllerClass(Class<? extends Controller> klass) throws InstantiationException, IllegalAccessException{
        if(klass.isAnnotationPresent(BeforeView.class)){
            addControlleBeforeRequest(klass);
        }else{
            addControllerToView(dymanicServlet, klass);
        }
    }

    private void addControllerToView(Dynamic dynamic, Class<? extends Controller> klass) throws IllegalAccessException, InstantiationException {
        System.out.println(klass);
        RequestReceiver controllerInfo = klass.getAnnotation(RequestReceiver.class);
        System.out.println("info: "+controllerInfo);
        umbrellaServlet.addController(controllerInfo.url(), klass.newInstance());
        dynamic.addMapping(controllerInfo.url());
    }

    private void addControlleBeforeRequest(Class<? extends Controller> klass) throws InstantiationException, IllegalAccessException{
        BeforeView beforeRequest = klass.getAnnotation(BeforeView.class);
        umbrellaFilter.addController(beforeRequest.value(), klass.newInstance());
    }

    public void contextDestroyed(ServletContextEvent sce) {

    }

}
