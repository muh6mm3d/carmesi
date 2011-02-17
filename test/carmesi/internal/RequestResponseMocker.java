package carmesi.internal;

import java.util.Iterator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import static org.mockito.Mockito.*;

/**
 *
 * @author Victor
 */
public class RequestResponseMocker implements MethodRule {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private Map<String, String[]> parameters=new HashMap<String, String[]>();

    private void createMocks() {
        request=mock(HttpServletRequest.class);
        response=mock(HttpServletResponse.class);
        when(request.getParameterMap()).thenAnswer(new Answer<Map<String, String[]>>(){

            public Map<String, String[]> answer(InvocationOnMock invocation) throws Throwable {
                return parameters;
            }
            
        });
        when(request.getParameterNames()).thenAnswer(new Answer<Enumeration<String>>(){

            public Enumeration<String> answer(InvocationOnMock invocation) throws Throwable {
                return new EnumerationAdapter(parameters.keySet().iterator());
            }
            
        });
        
        when(request.getParameterValues(anyString())).thenAnswer(new Answer<String[]>(){

            public String[] answer(InvocationOnMock invocation) throws Throwable {
                return parameters.get(invocation.getArguments()[0].toString());
            }
            
        });
        
        when(request.getParameter(anyString())).thenAnswer(new Answer<String>(){

            public String answer(InvocationOnMock invocation) throws Throwable {
                String[] values=parameters.get(invocation.getArguments()[0].toString());
                return values != null ? values[0] : null;
            }
            
        });
        
    }

    public HttpServletRequest getRequest(){
        return request;
    }
    
    public HttpServletResponse getResponse(){
        return response;
    }
    
    public void setRequestParameter(String name, String value){
        parameters.put(name, new String[]{value});
    }
    
    public void setRequestParameters(String name, String... values){
        parameters.put(name, values);
    }
    
//    public void setRequestParameters(Map<String, String[]> map){
//        parameters.putAll(map);
//    }

    public Statement apply(final Statement base, FrameworkMethod method, Object target) {
        return new Statement()  {

            @Override
            public void evaluate() throws Throwable {
                createMocks();
                try {
                    base.evaluate();
                } finally {
                    
                }
            }
        };
    }
    
    private static class EnumerationAdapter implements  Enumeration<String> {
        private Iterator<String> iterator;

        public EnumerationAdapter(Iterator<String> iterator) {
            this.iterator = iterator;
        }

        public boolean hasMoreElements() {
            return iterator.hasNext();
        }

        public String nextElement() {
            return iterator.next();
        }
        
    }
    
}
