/*
 */

package carmesi.internal;

import carmesi.HttpMethod;
import carmesi.URL;
import carmesi.RedirectTo;
import javax.servlet.ServletConfig;
import org.junit.Rule;
import org.junit.Test;
import static org.mockito.Mockito.*;

/**
 *
 * @author Victor
 */
public class TestRedirect {
    @Rule
    public RequestResponseMocker mocker=new RequestResponseMocker();
    
    @Test
    public void shouldRedirect() throws Exception{
        ControllerServlet servlet=new ControllerServlet(DynamicController.createDynamicController(new RedirectController()));
        ServletConfig servletConfig = mock(ServletConfig.class);
        servlet.init(servletConfig);
        when(mocker.getRequest().getMethod()).thenReturn("GET");
        when(mocker.getRequest().getContextPath()).thenReturn("/MyPath");
        servlet.service(mocker.getRequest(), mocker.getResponse());
        verify(mocker.getResponse()).sendRedirect("/MyPath/viewRedirect.jsp");
    }
    
    @URL("/any")
    @RedirectTo("/viewRedirect.jsp")
    static class RedirectController{
        
        public void doAction(){
            
        }
        
    }

}
