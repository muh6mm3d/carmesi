package carmesi.internal;

import java.util.List;
import carmesi.internal.dynamic.DynamicControllerServlet;
import org.junit.Test;
import java.util.EnumSet;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletRegistration;
import javax.servlet.Servlet;
import carmesi.BeforeURL;
import carmesi.Controller;
import carmesi.RedirectTo;
import carmesi.URL;
import carmesi.convertion.Converter;
import carmesi.convertion.ConverterFor;
import carmesi.convertion.TargetInfo;
import carmesi.internal.dynamic.DynamicController;
import java.awt.Point;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Annotation processing must be disabled.
 * 
 * @author Victor
 */
public class RegistratorListenerTest {
    @Rule
    public TemporaryFolder folder=new TemporaryFolder();

    public RegistratorListenerTest() {
    }

    @Test
    public void shouldCreateControllers() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException{
        File file=folder.newFile("carmesi.list");
        BufferedWriter writer=new BufferedWriter(new FileWriter(file));
        Class classes[]={TypesafeController.class, TypesafeControllerBefore.class, MyDynamicController.class, MyDynamicControllerBefore.class, MyConverter.class};
        List<ControllerInfo> infos=new LinkedList<ControllerInfo>();
        for(Class c:classes){
            writer.write(c.getCanonicalName());
            writer.newLine();
            if(!Converter.class.isAssignableFrom(c)){
                infos.add(ControllerInfo.createInfo(c));
            }
        }
        writer.close();
        
        ServletContext servletContext=mock(ServletContext.class);
        ControllerFactory factory=spy(new SimpleControllerFactory());
        
        ServletRegistration.Dynamic dynamicServlet=mock(ServletRegistration.Dynamic.class);
        when(servletContext.addServlet(anyString(), (Servlet)any())).thenReturn(dynamicServlet);
        
        FilterRegistration.Dynamic dynamicFilter=mock(FilterRegistration.Dynamic.class);
        when(servletContext.addFilter(anyString(), (Filter)any())).thenReturn(dynamicFilter);
        
        RegistratorListener registrator=new RegistratorListener(factory, file.toURI().toURL());
        registrator.contextInitialized(new ServletContextEvent(servletContext));
        
        ArgumentCaptor<DynamicControllerServlet> servletCaptor=ArgumentCaptor.forClass(DynamicControllerServlet.class);
        ArgumentCaptor<ControllerFilter> filterCaptor=ArgumentCaptor.forClass(ControllerFilter.class);
        
//        verify(servletContext, times(2)).addServlet(anyString(), (Servlet)any());
//        verify(servletContext, times(2)).addFilter(anyString(), (Filter)any());
        verify(servletContext).addServlet(eq(MyDynamicController.class.getSimpleName()), servletCaptor.capture());
        verify(servletContext).addFilter(eq(MyDynamicControllerBefore.class.getSimpleName()), filterCaptor.capture());
        
        DynamicController controller=(DynamicController) servletCaptor.getValue().getController();
        DynamicController controllerBefore=(DynamicController) filterCaptor.getValue().getController();
        
        assertThat(controller.getConverter(Point.class), is(MyConverter.class));
        assertThat(controllerBefore.getConverter(Point.class), is(MyConverter.class));
        EnumSet<DispatcherType> set = EnumSet.of(DispatcherType.REQUEST);
        
        for(ControllerInfo info:infos){
            if(info.isBefore){
                verify(dynamicFilter).addMappingForUrlPatterns(set, false, info.url);
            }else{
                verify(dynamicServlet).addMapping(info.url);
            }
        }
        
        for(Class c:classes){
            if(!Converter.class.isAssignableFrom(c)){
                verify(factory).createController(c);
            }
        }
    }
    
    static class ControllerInfo{
        private String classCanonicalName;
        private String url;
        private boolean isBefore;

        public ControllerInfo(String classCanonicalName, String url) {
            this.classCanonicalName = classCanonicalName;
            this.url = url;
        }
        
        public static ControllerInfo createInfo(Class<Object> controllerClass){
            String url=null;
            boolean before=false;
            if(controllerClass.isAnnotationPresent(URL.class)){
                url=controllerClass.getAnnotation(URL.class).value();
            }else if(controllerClass.isAnnotationPresent(BeforeURL.class)){
                url=controllerClass.getAnnotation(BeforeURL.class).value();
                before=true;
            }
            ControllerInfo controllerInfo = new ControllerInfo(controllerClass.getCanonicalName(), url);
            controllerInfo.isBefore=before;
            return controllerInfo;
        }
        
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
    public static class MyDynamicController{

        public void doAction(){
            
        }
        
    }
    
    @BeforeURL("/dynamicBefore")
    public static class MyDynamicControllerBefore{

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