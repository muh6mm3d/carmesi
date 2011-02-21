/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package carmesi.internal.simplecontrollers;

import carmesi.internal.simplecontrollers.SimpleControllerWrapper;
import org.junit.Rule;
import org.mockito.ArgumentCaptor;
import carmesi.ApplicationAttribute;
import carmesi.CookieValue;
import carmesi.SessionAttribute;
import carmesi.RequestAttribute;
import carmesi.internal.RequestResponseMocker;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author Victor Hugo Herrera Maldonado
 */
public class TestProcessReturnValues {
    @Rule
    public RequestResponseMocker mocker=new RequestResponseMocker();

    @Test
    public void shouldHaveRequestAttribute() throws Exception{
        SimpleControllerWrapper controller=SimpleControllerWrapper.createInstance(new Object(){
        
            public int getValue(){
                return 10;
            }
        
        });
        
        controller.execute(mocker.getRequest(), mocker.getResponse());
        verify(mocker.getRequest()).setAttribute("value", 10);
    }
    
    @Test
    public void shouldNotHaveRequestAttribute() throws Exception{
        SimpleControllerWrapper controller=SimpleControllerWrapper.createInstance(new Object(){
        
            public int value(){
                return 10;
            }
        
        });
        controller.execute(mocker.getRequest(), mocker.getResponse());
        verify(mocker.getRequest(), never()).setAttribute("value", 10);
    }
    
    @Test
    public void shouldHaveRequestAttributeToo() throws Exception{
        SimpleControllerWrapper controller=SimpleControllerWrapper.createInstance(new Object(){
            
            @RequestAttribute("result")
            public int sum(){
                return 1+2;
            }
            
        });
        controller.execute(mocker.getRequest(), mocker.getResponse());
        verify(mocker.getRequest()).setAttribute("result", 3);
    }
    
    @Test
    public void shouldHaveSessionAttribute() throws Exception{
        HttpSession session=mock(HttpSession.class);
        SimpleControllerWrapper controller=SimpleControllerWrapper.createInstance(new Object(){
            
            @SessionAttribute("result")
            public int sum(){
                return 1+2;
            }
            
        });
        when(mocker.getRequest().getSession()).thenReturn(session);
        controller.execute(mocker.getRequest(), mocker.getResponse());
        verify(session).setAttribute("result", 3);
    }
    
    @Test
    public void shouldHaveApplicationAttribute() throws Exception{
        ServletContext context=mock(ServletContext.class);
        SimpleControllerWrapper controller=SimpleControllerWrapper.createInstance(new Object(){
            
            @ApplicationAttribute("result")
            public int sum(){
                return 1+2;
            }
            
        });
        when(mocker.getRequest().getServletContext()).thenReturn(context);
        controller.execute(mocker.getRequest(), mocker.getResponse());
        verify(context).setAttribute("result", 3);
    }
    
    @Test
    public void shouldHaveCookie() throws Exception{
        ServletContext context=mock(ServletContext.class);
        SimpleControllerWrapper controller=SimpleControllerWrapper.createInstance(new Object(){
            
            @CookieValue("result")
            public int sum(){
                return 1+2;
            }
            
        });
        when(mocker.getRequest().getServletContext()).thenReturn(context);
        controller.execute(mocker.getRequest(), mocker.getResponse());
        ArgumentCaptor<Cookie> argument = ArgumentCaptor.forClass(Cookie.class);
        verify(mocker.getResponse()).addCookie(argument.capture());
        assertThat(argument.getValue().getValue(), is("3"));
        assertThat(argument.getValue().getName(), is("result"));
        assertThat(argument.getValue().getMaxAge(), is(-1));
    }
    
    @Test
    public void shouldHaveCookieToo() throws Exception{
        ServletContext context=mock(ServletContext.class);
        SimpleControllerWrapper controller=SimpleControllerWrapper.createInstance(new Object(){
            
            public Cookie sum(){
                Cookie cookie = new Cookie("result", String.valueOf(1+2));
                cookie.setMaxAge(156);
                return cookie;
            }
            
        });
        when(mocker.getRequest().getServletContext()).thenReturn(context);
        controller.execute(mocker.getRequest(), mocker.getResponse());
        ArgumentCaptor<Cookie> argument = ArgumentCaptor.forClass(Cookie.class);
        verify(mocker.getResponse()).addCookie(argument.capture());
        assertThat(argument.getValue().getValue(), is("3"));
        assertThat(argument.getValue().getName(), is("result"));
        assertThat(argument.getValue().getMaxAge(), is(156));
    }
    
}
