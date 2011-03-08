/* Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0) */

package carmesi.internal;

import carmesi.internal.simplecontrollers.SimpleControllerWrapper;
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
        ControllerServlet servlet=new ControllerServlet(SimpleControllerWrapper.createInstance(new SimpleForwardController()));
        servlet.setValidHttpMethods(HttpMethod.values());
        servlet.setAfterControllerAction(new ControllerServlet.ForwardAction("/viewForward.jsp"));
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
        ControllerServlet servlet=new ControllerServlet(new TypesafeForwardController());
        servlet.setValidHttpMethods(HttpMethod.values());
        servlet.setAfterControllerAction(new ControllerServlet.ForwardAction("/viewForward.jsp"));
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
