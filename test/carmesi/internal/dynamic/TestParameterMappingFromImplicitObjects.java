/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package carmesi.internal.dynamic;

import carmesi.internal.RequestResponseMocker;
import carmesi.internal.dynamic.DynamicController;
import org.junit.Rule;
import javax.servlet.ServletContext;
import org.junit.Before;
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
public class TestParameterMappingFromImplicitObjects {
    private boolean invoked;
    
    @Rule
    public RequestResponseMocker mocker=new RequestResponseMocker();
    
    @Before
    public void init(){
        invoked=false;
    }
    
    @Test
    public void shouldBeInvokedWithRequest() throws Exception{
        DynamicController controller=DynamicController.createDynamicController(new Object(){
            
            public void doAction(HttpServletRequest r){
                invoked=true;
                assertThat(r, is(mocker.getRequest()));
            }
            
        });
        controller.execute(mocker.getRequest(), mocker.getResponse());
        assertTrue(invoked);
    }
    
    @Test
    public void shouldBeInvokedWithResponse() throws Exception{
        DynamicController controller=DynamicController.createDynamicController(new Object(){
            
            public void doAction(HttpServletResponse r){
                invoked=true;
                assertThat(r, is(mocker.getResponse()));
            }
            
        });
        controller.execute(mocker.getRequest(), mocker.getResponse());
        assertTrue(invoked);
    }
    
    @Test
    public void shouldBeInvokedWithSession() throws Exception{
        final HttpSession session=mock(HttpSession.class);
        when(mocker.getRequest().getSession()).thenReturn(session);
        DynamicController controller=DynamicController.createDynamicController(new Object(){
            
            public void doAction(HttpSession s){
                invoked=true;
                assertThat(s, is(session));
            }
            
        });
        controller.execute(mocker.getRequest(), mocker.getResponse());
        assertTrue(invoked);
    }
    
    @Test
    public void shouldBeInvokedWithContext() throws Exception{
        final ServletContext context=mock(ServletContext.class);
        when(mocker.getRequest().getServletContext()).thenReturn(context);
        DynamicController controller=DynamicController.createDynamicController(new Object(){
            
            public void doAction(ServletContext c){
                invoked=true;
                assertThat(c, is(context));
            }
            
        });
        controller.execute(mocker.getRequest(), mocker.getResponse());
        assertTrue(invoked);
    }

}
