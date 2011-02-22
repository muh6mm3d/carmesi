/* Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0) */

package carmesi.internal.simplecontrollers;

import carmesi.internal.simplecontrollers.SimpleControllerWrapper;
import carmesi.CookieValue;
import carmesi.convert.DatePattern;
import carmesi.internal.RequestResponseMocker;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.Cookie;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author Hugo Herrera Maldonado
 */
public class TestCookieValue {
    private boolean invoked=false;
    
    @Rule
    public RequestResponseMocker mocker=new RequestResponseMocker();
    
    @Test
    public void shouldConvertFromAndToCookie() throws Exception{
        final String stringDate="01.12.2010";
        final SimpleDateFormat format=new SimpleDateFormat("dd.MM.yyyy");
        SimpleControllerWrapper controller=SimpleControllerWrapper.createInstance(new Object(){
            
            @CookieValue("date2") @DatePattern("dd.MM.yyyy")
            public Date doAction(@CookieValue("date") @DatePattern("dd.MM.yyyy") Date date){
                try{
                    invoked=true;
                    assertThat(date, is(format.parse(stringDate)));
                    return date;
                }catch(ParseException ex){
                    throw new AssertionError(ex.toString());
                }
            }
            
            
        });
        when(mocker.getRequest().getCookies()).thenReturn(new Cookie[]{new Cookie("date", stringDate)});
        controller.executeAndGetResult(mocker.getRequest(), mocker.getResponse());
        ArgumentCaptor<Cookie> cookieCaptor=ArgumentCaptor.forClass(Cookie.class);
        verify(mocker.getResponse()).addCookie(cookieCaptor.capture());
        assertThat(cookieCaptor.getValue().getName(), is("date2"));
        assertThat(cookieCaptor.getValue().getValue(), is(stringDate));
        System.out.println("cookie value: "+cookieCaptor.getValue().getValue());
        assertTrue(invoked);
    }
    
    
    @Test
    public void shouldConvertFromAndToCookieToo() throws Exception{
        final String stringA="whatever";
        SimpleControllerWrapper controller=SimpleControllerWrapper.createInstance(new Object(){
            
            @CookieValue("result")
            public A doAction(@CookieValue("date") @DatePattern("dd.MM.yyyy") A a){
                invoked=true;
                A a2=new A();
                a2.content=reverse(a.content);
                return a2;
            }
            
            
        });
        when(mocker.getRequest().getCookies()).thenReturn(new Cookie[]{new Cookie("date", stringA)});
        controller.executeAndGetResult(mocker.getRequest(), mocker.getResponse());
        ArgumentCaptor<Cookie> cookieCaptor=ArgumentCaptor.forClass(Cookie.class);
        verify(mocker.getResponse()).addCookie(cookieCaptor.capture());
        assertThat(cookieCaptor.getValue().getName(), is("result"));
        assertThat(cookieCaptor.getValue().getValue(), is(reverse(stringA)));
        System.out.println("cookie value too: "+cookieCaptor.getValue().getValue());
        assertTrue(invoked);
    }
    
    public static class A{
        private String content;

        @Override
        public String toString() {
            return content;
        }
        
        public static A valueOf(String s){
            A a=new A();
            a.content=s;
           return  a;
        }
        
    }
    
    private static String reverse(String s){
        StringBuilder builder=new StringBuilder();
        for(int i=s.length()-1; i >= 0; i--){
            builder.append(s.charAt(i));
        }
        return builder.toString();
    }

}
