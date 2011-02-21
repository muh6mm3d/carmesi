/*
 */

package carmesi.internal;

import carmesi.internal.dynamic.DynamicController;
import carmesi.Controller;
import carmesi.URL;
import javax.servlet.ServletConfig;
import carmesi.ForwardTo;
import carmesi.HttpMethod;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Rule;
import org.junit.Test;
import static org.mockito.Mockito.*;

/**
 *
 * @author Victor Hugo Herrera Maldonado
 */
public class TestForward {
    @Rule
    public RequestResponseMocker mocker=new RequestResponseMocker();
    
    @Test
    public void shouldForwardTo() throws Exception{
        RequestDispatcher dispatcher=mock(RequestDispatcher.class);
        ControllerServlet servlet=ControllerServlet.createInstanceWithForward(DynamicController.createDynamicController(new SimpleForwardController()), "/viewForward.jsp", HttpMethod.values());
//                new DynamicControllerServlet(DynamicController.createDynamicController(new SimpleForwardController()));
        ServletConfig servletConfig = mock(ServletConfig.class);
        servlet.init(servletConfig);
        when(mocker.getRequest().getMethod()).thenReturn("GET");
        when(mocker.getRequest().getRequestDispatcher("/viewForward.jsp")).thenReturn(dispatcher);
        servlet.service(mocker.getRequest(), mocker.getResponse());
        verify(dispatcher).forward(mocker.getRequest(), mocker.getResponse());
    }
    
    @Test
    public void shouldForwardToToo() throws Exception{
        RequestDispatcher dispatcher=mock(RequestDispatcher.class);
        ControllerServlet servlet=ControllerServlet.createInstanceWithForward(new TypesafeForwardController(), "/viewForward.jsp", HttpMethod.values());
        ServletConfig servletConfig = mock(ServletConfig.class);
        servlet.init(servletConfig);
        when(mocker.getRequest().getMethod()).thenReturn("GET");
        when(mocker.getRequest().getRequestDispatcher("/viewForward.jsp")).thenReturn(dispatcher);
        servlet.service(mocker.getRequest(), mocker.getResponse());
        verify(dispatcher).forward(mocker.getRequest(), mocker.getResponse());
    }
    
    @URL("/any")
    @ForwardTo("/viewForward.jsp")
    public static class SimpleForwardController{
        
        public void doAction(){
            
        }
        
    }
    
    @URL("/any")
    @ForwardTo("/viewForward.jsp")
    public static class TypesafeForwardController implements Controller{
        
        public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            
        }
        
    }

}
