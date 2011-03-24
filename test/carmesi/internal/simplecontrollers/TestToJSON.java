/* Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0) */

package carmesi.internal.simplecontrollers;

import carmesi.json.JSONSerializer;
import java.io.PrintWriter;
import carmesi.ToJSON;
import carmesi.internal.RequestResponseMocker;
import org.junit.Rule;
import org.junit.Test;
import static org.mockito.Mockito.*;

/**
 *
 * @author Victor Hugo Herrera Maldonado
 */
public class TestToJSON {
    @Rule
    public RequestResponseMocker mocker=new RequestResponseMocker();
    
    @Test
    public void shouldReturnJSON() throws Exception{
        SimpleControllerWrapper simpleController=SimpleControllerWrapper.createInstance(new Object(){
            
            @ToJSON
            public A getA(){
                return new A("x");
            }
            
        });
        JSONSerializer serializer=mock(JSONSerializer.class);
        simpleController.setJSONSerializer(serializer);
        when(serializer.serialize(new A("x"))).thenReturn("{a:'x'}");
        PrintWriter writer=mock(PrintWriter.class);
        when(mocker.getResponse().getWriter()).thenReturn(writer);
        simpleController.execute(mocker.getRequest(), mocker.getResponse());
        
        verify(serializer, times(1)).serialize(new A("x"));
        verify(writer).println("{a:'x'}");
    }
    
    static class A{
        private String field1;

        public A(String field1) {
            this.field1 = field1;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final A other = (A) obj;
            if ((this.field1 == null) ? (other.field1 != null) : !this.field1.equals(other.field1)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 61 * hash + (this.field1 != null ? this.field1.hashCode() : 0);
            return hash;
        }
        
    }
    
}
