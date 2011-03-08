/* Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0) */

package carmesi.internal;

import carmesi.Controller;
import carmesi.HttpMethod;
import carmesi.internal.simplecontrollers.SimpleControllerWrapper;
import carmesi.URL;
import carmesi.RedirectTo;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Rule;
import org.junit.Test;
import static org.mockito.Mockito.*;

/**
 *
 * @author Victor Hugo Herrera Maldonado
 */
public class TestRedirect {
    @Rule
    public RequestResponseMocker mocker=new RequestResponseMocker();
    
    @Test
    public void shouldRedirect() throws Exception{
        ControllerServlet servlet=new ControllerServlet(SimpleControllerWrapper.createInstance(new SimpleRedirectController()));
        servlet.setValidHttpMethods(HttpMethod.values());
        servlet.setAfterControllerAction(new ControllerServlet.RedirectAction("/viewRedirect.jsp"));
        ServletConfig servletConfig = mock(ServletConfig.class);
        servlet.init(servletConfig);
        when(mocker.getRequest().getMethod()).thenReturn("GET");
        when(mocker.getRequest().getContextPath()).thenReturn("/MyPath");
        servlet.service(mocker.getRequest(), mocker.getResponse());
        verify(mocker.getResponse()).sendRedirect("/MyPath/viewRedirect.jsp");
    }
    
    @Test
    public void shouldRedirectToo() throws Exception{
        ControllerServlet servlet=new ControllerServlet(new TypesafeRedirectController());
        servlet.setValidHttpMethods(HttpMethod.values());
        servlet.setAfterControllerAction(new ControllerServlet.RedirectAction("/viewRedirect.jsp"));
        ServletConfig servletConfig = mock(ServletConfig.class);
        servlet.init(servletConfig);
        when(mocker.getRequest().getMethod()).thenReturn("GET");
        when(mocker.getRequest().getContextPath()).thenReturn("/MyPath");
        servlet.service(mocker.getRequest(), mocker.getResponse());
        verify(mocker.getResponse()).sendRedirect("/MyPath/viewRedirect.jsp");
    }
    
    @URL("/any")
    @RedirectTo("/viewRedirect.jsp")
    public static class SimpleRedirectController{
        
        public void doAction(){
            
        }
        
    }
    
    @URL("/any")
    @RedirectTo("/viewRedirect.jsp")
    public static class TypesafeRedirectController implements  Controller{
        
        public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
         
        }
        
    }

}
