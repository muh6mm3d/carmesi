/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package carmesi.internal;

import carmesi.internal.TestParameterMappingFromRequestParametersCustom.Person;
import org.mockito.ArgumentCaptor;
import carmesi.ApplicationAttribute;
import carmesi.CookieValue;
import carmesi.SessionAttribute;
import carmesi.RequestAttribute;
import carmesi.internal.ControllerWrapper.Result;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author Victor
 */
public class TestProcessReturnValues {

    @Test
    public void shouldHaveRequestAttribute() throws Exception{
        DynamicController controller=DynamicController.createDynamicController(new Object(){
        
            public int getValue(){
                return 10;
            }
        
        });
        HttpServletRequest request=mock(HttpServletRequest.class);
        HttpServletResponse response=mock(HttpServletResponse.class);
        Result result = controller.execute(request, response);
        result.process();
        verify(request).setAttribute("value", 10);
    }
    
    @Test
    public void shouldNotHaveRequestAttribute() throws Exception{
        DynamicController controller=DynamicController.createDynamicController(new Object(){
        
            public int value(){
                return 10;
            }
        
        });
        HttpServletRequest request=mock(HttpServletRequest.class);
        HttpServletResponse response=mock(HttpServletResponse.class);
        Result result = controller.execute(request, response);
        result.process();
        verify(request, never()).setAttribute("value", 10);
    }
    
    @Test
    public void shouldHaveRequestAttributeToo() throws Exception{
        DynamicController controller=DynamicController.createDynamicController(new Object(){
            
            @RequestAttribute("result")
            public int sum(){
                return 1+2;
            }
            
        });
        HttpServletRequest request=mock(HttpServletRequest.class);
        HttpServletResponse response=mock(HttpServletResponse.class);
        Result result = controller.execute(request, response);
        result.process();
        verify(request).setAttribute("result", 3);
    }
    
    @Test
    public void shouldHaveSessionAttribute() throws Exception{
        HttpSession session=mock(HttpSession.class);
        DynamicController controller=DynamicController.createDynamicController(new Object(){
            
            @SessionAttribute("result")
            public int sum(){
                return 1+2;
            }
            
        });
        HttpServletRequest request=mock(HttpServletRequest.class);
        when(request.getSession()).thenReturn(session);
        HttpServletResponse response=mock(HttpServletResponse.class);
        Result result = controller.execute(request, response);
        result.process();
        verify(session).setAttribute("result", 3);
    }
    
    @Test
    public void shouldHaveApplicationAttribute() throws Exception{
        ServletContext context=mock(ServletContext.class);
        DynamicController controller=DynamicController.createDynamicController(new Object(){
            
            @ApplicationAttribute("result")
            public int sum(){
                return 1+2;
            }
            
        });
        HttpServletRequest request=mock(HttpServletRequest.class);
        when(request.getServletContext()).thenReturn(context);
        HttpServletResponse response=mock(HttpServletResponse.class);
        Result result = controller.execute(request, response);
        result.process();
        verify(context).setAttribute("result", 3);
    }
    
    @Test
    public void shouldHaveCookie() throws Exception{
        ServletContext context=mock(ServletContext.class);
        DynamicController controller=DynamicController.createDynamicController(new Object(){
            
            @CookieValue("result")
            public int sum(){
                return 1+2;
            }
            
        });
        HttpServletRequest request=mock(HttpServletRequest.class);
        when(request.getServletContext()).thenReturn(context);
        HttpServletResponse response=mock(HttpServletResponse.class);
        Result result = controller.execute(request, response);
        result.process();
        ArgumentCaptor<Cookie> argument = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(argument.capture());
        assertThat(argument.getValue().getValue(), is("3"));
        assertThat(argument.getValue().getName(), is("result"));
        assertThat(argument.getValue().getMaxAge(), is(-1));
    }
    
    @Test
    public void shouldHaveCookieToo() throws Exception{
        ServletContext context=mock(ServletContext.class);
        DynamicController controller=DynamicController.createDynamicController(new Object(){
            
            public Cookie sum(){
                Cookie cookie = new Cookie("result", String.valueOf(1+2));
                cookie.setMaxAge(156);
                return cookie;
            }
            
        });
        HttpServletRequest request=mock(HttpServletRequest.class);
        when(request.getServletContext()).thenReturn(context);
        HttpServletResponse response=mock(HttpServletResponse.class);
        Result result = controller.execute(request, response);
        result.process();
        ArgumentCaptor<Cookie> argument = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(argument.capture());
        assertThat(argument.getValue().getValue(), is("3"));
        assertThat(argument.getValue().getName(), is("result"));
        assertThat(argument.getValue().getMaxAge(), is(156));
    }
    
}
