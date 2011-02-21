package carmesi.internal.simplecontrollers;

import carmesi.internal.simplecontrollers.SimpleControllerWrapper;
import org.junit.Rule;
import org.junit.Before;
import carmesi.ApplicationAttribute;
import carmesi.SessionAttribute;
import carmesi.RequestAttribute;
import carmesi.internal.RequestResponseMocker;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author Victor Hugo Herrera Maldonado
 */
public class TestParameterMappingFromAttributes {
    private boolean invoked;
    
    @Rule
    public RequestResponseMocker mocker=new RequestResponseMocker();
    
    @Before
    public void init(){
        invoked=false;
    }
    
    @Test
    public void shouldHaveRequestAttribute() throws Exception{
        SimpleControllerWrapper controller=SimpleControllerWrapper.createInstance(new Object(){
            
            public void doAction(@RequestAttribute("number") int number){
                assertThat(number, is(8));
                invoked=true;
            }
            
        });
        when(mocker.getRequest().getAttribute("number")).thenReturn(8);
        controller.execute(mocker.getRequest(), mocker.getResponse());
        verify(mocker.getRequest()).getAttribute("number");
        assertTrue(invoked);
    }
    
    @Test
    public void shouldHaveSessionAttribute() throws Exception{
        SimpleControllerWrapper controller=SimpleControllerWrapper.createInstance(new Object(){
            
            public void doAction(@SessionAttribute("number") int number){
                assertThat(number, is(8));
                invoked=true;
            }
            
        });
        HttpSession session=mock(HttpSession.class);
        when(mocker.getRequest().getSession()).thenReturn(session);
        when(session.getAttribute("number")).thenReturn(8);
        controller.execute(mocker.getRequest(), mocker.getResponse());
        verify(session).getAttribute("number");
        assertTrue(invoked);
    }
    
    @Test
    public void shouldHaveApplicationAttribute() throws Exception{
        SimpleControllerWrapper controller=SimpleControllerWrapper.createInstance(new Object(){
            
            public void doAction(@ApplicationAttribute("number") int number){
                assertThat(number, is(8));
                invoked=true;
            }
            
        });
        ServletContext context=mock(ServletContext.class);
        when(mocker.getRequest().getServletContext()).thenReturn(context);
        when(context.getAttribute("number")).thenReturn(8);
        controller.execute(mocker.getRequest(), mocker.getResponse());
        verify(context).getAttribute("number");
        assertTrue(invoked);
    }

}
