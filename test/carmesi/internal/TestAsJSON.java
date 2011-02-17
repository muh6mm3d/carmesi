/*
 */

package carmesi.internal;

import carmesi.ToJSON;
import carmesi.internal.ControllerWrapper.Result;
import java.awt.Point;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.Rule;
import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 *
 * @author Victor
 */
public class TestAsJSON {
    @Rule
    public RequestResponseMocker mocker=new RequestResponseMocker();
    
    @Test
    public void shouldReturnJSON() throws Exception{
        DynamicController controller=DynamicController.createDynamicController(new Object(){
            
            @ToJSON
            public Point doAction(){
                return new Point(2,4);
            }
            
        });
        StringWriter writer=new StringWriter();
        when(mocker.getResponse().getWriter()).thenReturn(new PrintWriter(writer));
        writer.toString();
        Result result = controller.execute(mocker.getRequest(), mocker.getResponse());
        result.process();
        fail();
    }

}
