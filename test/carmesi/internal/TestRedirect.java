/*
 */

package carmesi.internal;

import carmesi.Controller;
import carmesi.HttpMethod;
import carmesi.internal.dynamic.DynamicController;
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
        ControllerServlet servlet=ControllerServlet.createInstanceWithRedirect(DynamicController.createDynamicController(new SimpleRedirectController()), "/viewRedirect.jsp", HttpMethod.values());
        ServletConfig servletConfig = mock(ServletConfig.class);
        servlet.init(servletConfig);
        when(mocker.getRequest().getMethod()).thenReturn("GET");
        when(mocker.getRequest().getContextPath()).thenReturn("/MyPath");
        servlet.service(mocker.getRequest(), mocker.getResponse());
        verify(mocker.getResponse()).sendRedirect("/MyPath/viewRedirect.jsp");
    }
    
    @Test
    public void shouldRedirectToo() throws Exception{
        ControllerServlet servlet=ControllerServlet.createInstanceWithRedirect(new TypesafeRedirectController(), "/viewRedirect.jsp", HttpMethod.values());
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
