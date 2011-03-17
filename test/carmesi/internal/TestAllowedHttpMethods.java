/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package carmesi.internal;

import carmesi.HttpMethod;
import carmesi.AllowedHttpMethods;
import carmesi.URL;
import carmesi.internal.simplecontrollers.SimpleControllerWrapper;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.junit.Rule;
import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 *
 * @author Victor
 */
public class TestAllowedHttpMethods {
    @Rule
    public RequestResponseMocker mocker=new RequestResponseMocker();
    
    @Test
    public void shouldNotBeAllowed() throws ServletException, Exception{
        RestrictedController controller = new RestrictedController();
        ControllerServlet servlet=new ControllerServlet(SimpleControllerWrapper.createInstance(controller));
        servlet.setValidHttpMethods(controller.getClass().getAnnotation(AllowedHttpMethods.class).value());
        ServletConfig servletConfig = mock(ServletConfig.class);
        servlet.init(servletConfig);
        when(mocker.getRequest().getMethod()).thenReturn("get");
        servlet.service(mocker.getRequest(), mocker.getResponse());
        verify(mocker.getResponse()).sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        assertFalse(controller.wasExecuted());
    }
    
    @Test
    public void shouldBeAllowed() throws Exception{
        RestrictedController controller = new RestrictedController();
        ControllerServlet servlet=new ControllerServlet(SimpleControllerWrapper.createInstance(controller));
        servlet.setValidHttpMethods(controller.getClass().getAnnotation(AllowedHttpMethods.class).value());
        ServletConfig servletConfig = mock(ServletConfig.class);
        servlet.init(servletConfig);
        when(mocker.getRequest().getMethod()).thenReturn("post");
        servlet.service(mocker.getRequest(), mocker.getResponse());
        assertTrue(controller.wasExecuted());
    }
    
    @URL("/controller")
    @AllowedHttpMethods(HttpMethod.POST)
    public static class RestrictedController{
        private boolean executed;
     
        public void execute(){
            executed=true;
        }
        
        boolean wasExecuted(){
            return executed;
        }
        
    }

}
