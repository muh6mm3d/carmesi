/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package carmesi.internal.simplecontrollers;

import carmesi.internal.simplecontrollers.SimpleControllerWrapper;
import carmesi.convert.TargetInfo;
import carmesi.convert.Converter;
import java.lang.annotation.RetentionPolicy;
import carmesi.convert.DatePattern;
import java.util.Date;
import org.junit.Rule;
import carmesi.RequestParameter;
import carmesi.internal.RequestResponseMocker;
import java.lang.annotation.Retention;
import java.text.SimpleDateFormat;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author Victor Hugo Herrera Maldonado
 */
public class TestParameterMappingFromRequestParameters {
    private boolean invoked;
    
    @Rule
    public RequestResponseMocker mocker=new RequestResponseMocker();
    
    @Before
    public void init(){
        invoked=false;
    }
    
    @Test
    public void shouldBeInvokedWithIntFromRequestParameter() throws Exception{
        SimpleControllerWrapper controller=SimpleControllerWrapper.createInstance(new Object(){
            
            public void doAction(@RequestParameter("number") int number){
                assertThat(number, is(3));
                invoked=true;
            }
            
        });
        mocker.setRequestParameter("number", "3");
        controller.execute(mocker.getRequest(), mocker.getResponse());
        assertTrue(invoked);
    }
    
    @Test
    public void shouldBeInvokedWithStringFromRequestParameter() throws Exception{
        SimpleControllerWrapper controller=SimpleControllerWrapper.createInstance(new Object(){
            
            public void doAction(@RequestParameter("string") String string){
                assertThat(string, is("xyz"));
                invoked=true;
            }
            
        });
        mocker.setRequestParameter("string", "xyz");
        controller.execute(mocker.getRequest(), mocker.getResponse());
        assertTrue(invoked);
    }
    
    @Test
    public void shouldBeInvokedWithArrayFromRequestParameter() throws Exception{
        SimpleControllerWrapper controller=SimpleControllerWrapper.createInstance(new Object(){
            
            public void doAction(@RequestParameter("array") long[] array){
                assertThat(array.length, is(3));
                assertThat(array[0], is(1L));
                assertThat(array[1], is(2L));
                assertThat(array[2], is(3L));
                invoked=true;
            }
            
        });
        when(mocker.getRequest().getParameterValues("array")).thenReturn(new String[]{"1","2","3"});
        controller.execute(mocker.getRequest(), mocker.getResponse());
        assertTrue(invoked);
    }
    
    @Test
    public void shouldBeInvokedWithCustomClassFromRequestParameter() throws Exception{
        SimpleControllerWrapper controller=SimpleControllerWrapper.createInstance(new Object(){
            
            public void doAction(@RequestParameter("custom") MyClass object){
                assertThat(object.getString(), is("xyz"));
                invoked=true;
            }
            
        });
        mocker.setRequestParameter("custom", "xyz");
        controller.execute(mocker.getRequest(), mocker.getResponse());
        assertTrue(invoked);
    }
    
    @Test
    public void shouldBeInvokedWithDateRequestParameter() throws Exception{
        SimpleControllerWrapper controller=SimpleControllerWrapper.createInstance(new Object(){
            
            public void doAction(@RequestParameter("custom") @DatePattern("dd/MM/yyyy") Date date){
                SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
                assertThat(format.format(date), is("2001-12-01"));
                invoked=true;
            }
            
        });
        mocker.setRequestParameter("custom", "1/12/2001");
        controller.execute(mocker.getRequest(), mocker.getResponse());
        assertTrue(invoked);
    }
    
    @Test
    public void shouldBeInvokedWithCustomConverterRequestParameter() throws Exception{
        SimpleControllerWrapper controller=SimpleControllerWrapper.createInstance(new Object(){
            
            public void doAction(@RequestParameter("custom") @MyAnnotation1 @MyAnnotation2 MyClass o){
                assertThat(o.getString(), is("qwerty"));
                invoked=true;
            }
            
        });
        controller.addConverter(MyClass.class, new Converter<MyClass>(){

            public MyClass convertToObject(String stringValue, TargetInfo info) {
                assertThat(stringValue, is("value"));
                assertNotNull(info.getAnnotation(MyAnnotation1.class));
                assertNotNull(info.getAnnotation(MyAnnotation2.class));
                return new MyClass("qwerty");
            }

            public String convertToString(MyClass value, TargetInfo info) {
                return null;
            }
            
        });
        mocker.setRequestParameter("custom", "value");
        controller.execute(mocker.getRequest(), mocker.getResponse());
        assertTrue(invoked);
    }
    
    static class MyClass{
        private String string;

        public MyClass(String string) {
            this.string = string;
        }
        
        public static MyClass valueOf(String string){
            return new MyClass(string);
        }

        public String getString() {
            return string;
        }
        
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    static @interface MyAnnotation1{
        
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    static @interface MyAnnotation2{
        
    }

}
