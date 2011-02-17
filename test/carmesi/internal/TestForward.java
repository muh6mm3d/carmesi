/*
 */

package carmesi.internal;

import carmesi.URL;
import carmesi.internal.TestRedirect.RedirectController;
import javax.servlet.ServletConfig;
import carmesi.ForwardTo;
import javax.servlet.RequestDispatcher;
import org.junit.Rule;
import org.junit.Test;
import static org.mockito.Mockito.*;

/**
 *
 * @author Victor
 */
public class TestForward {
    @Rule
    public RequestResponseMocker mocker=new RequestResponseMocker();
    
    @Test
    public void shouldForwardTo() throws Exception{
        RequestDispatcher dispatcher=mock(RequestDispatcher.class);
        ControllerServlet servlet=new ControllerServlet(DynamicController.createDynamicController(new ForwardController()));
        ServletConfig servletConfig = mock(ServletConfig.class);
        servlet.init(servletConfig);
        when(mocker.getRequest().getMethod()).thenReturn("GET");
        when(mocker.getRequest().getRequestDispatcher("/viewForward.jsp")).thenReturn(dispatcher);
        servlet.service(mocker.getRequest(), mocker.getResponse());
        verify(dispatcher).forward(mocker.getRequest(), mocker.getResponse());
    }
    
    @URL("/any")
    @ForwardTo("/viewForward.jsp")
    static class ForwardController{
        
        public void doAction(){
            
        }
        
    }

}
