/*
 */

package carmesi.internal.dynamic;

import carmesi.internal.RequestResponseMocker;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

/**
 *
 * @author Victor
 */
public class TestAsJSON {
    @Rule
    public RequestResponseMocker mocker=new RequestResponseMocker();
    
    @Test
    public void shouldReturnJSON() throws Exception{
        //not implemented because the json module will be reworked
        Assert.fail();
    }

}
