package carmesi.internal;

import carmesi.BeforeURL;
import carmesi.Controller;
import carmesi.RedirectTo;
import carmesi.URL;
import carmesi.convertion.Converter;
import carmesi.convertion.ConverterFor;
import carmesi.convertion.TargetInfo;
import java.awt.Point;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author Victor
 */
public class RegistratorListenerTest {
    @Rule
    public TemporaryFolder folder=new TemporaryFolder();

    public RegistratorListenerTest() {
    }

    @Test
    public void testContextInitialized() {
    }

    @Test
    public void testContextDestroyed() {
    }
    
    public void testCreateControllers() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException{
        throw new UnsupportedOperationException("Not yet implemented");
//        File file=folder.newFile("carmesi.list");
//        BufferedWriter writer=new BufferedWriter(new FileWriter(file));
//        Class classes[]={TypesafeController.class, TypesafeControllerBefore.class, MyDinamicController.class, MyConverter.class};
//        for(Class c:classes){
//            writer.write(c.getName());
//            writer.newLine();
//        }
//        writer.close();
//        FileInputStream input=new FileInputStream(file);
//        
//        RegistratorListener registrator=new RegistratorListener();
//        ServletContext servletContext=mock(ServletContext.class);
//        ControllerFactory factory=mock(SimpleControllerFactory.class);
//        registrator.contextInitialized(new ServletContextEvent(servletContext));
//        registrator.addClasses(input);
//        registrator.setControllerFactory(factory);
//        input.close();
    }
    
    @URL("/typesafe")
    @RedirectTo("/typesafeView.jsp")
    public static class TypesafeController implements  Controller{

        public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
    }
    
    @BeforeURL("/typesafeBefore")
    public static class TypesafeControllerBefore{

        public void doAction(){
            
        }
        
    }
    
    @URL("/dynamic")
    @RedirectTo("/dynamicView.jsp")
    public static class MyDinamicController{

        public void doAction(){
            
        }
        
    }
    
    @BeforeURL("/dynamicBefore")
    public static class MyDinamicControllerBefore{

        public void doAction(){
            
        }
        
    }
    
    @ConverterFor(Point.class)
    public static class MyConverter implements  Converter<Point>{

        public Point convert(String stringValue, TargetInfo info) {
            return null;
        }
        
    }

}