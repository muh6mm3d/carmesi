/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package carmesi.internal.dynamic;

import carmesi.internal.dynamic.DynamicController;
import java.util.Map;
import carmesi.RequestBean;
import carmesi.internal.RequestResponseMocker;
import java.util.HashMap;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author Victor
 */
public class TestParameterMappingFromRequestParametersCustom {
    private boolean invoked;
    
    @Rule
    public RequestResponseMocker mocker=new RequestResponseMocker();
    
    @Before
    public void init(){
        invoked=false;
    }
    
    @Test
    public void shouldBeInvokedWithBean() throws Exception{
        DynamicController controller=DynamicController.createDynamicController(new Object(){
            
            public void doAction(@RequestBean Person p){
               assertThat(p.getId(), is("t"));
               assertThat(p.getName(), is("Roberto"));
               assertThat(p.getAge(), is(30));
               assertThat(p.getCity(), is("Mexico"));
               invoked=true;
            }
            
        });
        mocker.setRequestParameter("id", "t");
        mocker.setRequestParameter("name", "Roberto");
        mocker.setRequestParameter("age", "30");
        mocker.setRequestParameter("city", "Mexico");
        controller.execute(mocker.getRequest(), mocker.getResponse());
        assertTrue(invoked);
    }
    
    @Test
    public void shouldBeInvokedWithBeanToo() throws Exception{
        DynamicController controller=DynamicController.createDynamicController(new Object(){
            
            public void doAction(@RequestBean Person2 p){
               assertThat(p.getId(), is("t"));
               assertThat(p.getCity(), is("Mexico"));
               assertNull(p.getName());
               assertThat(p.getAge(), is(0));
               assertArrayEquals(new int[]{3, 4, 5}, p.getNumbers());
               invoked=true;
            }
            
        });
        mocker.setRequestParameter("id", "t");
        mocker.setRequestParameter("city", "Mexico");
        mocker.setRequestParameters("numbers", "3", "4", "5");
        controller.execute(mocker.getRequest(), mocker.getResponse());
        assertTrue(invoked);
    }
    
    static class Person{
        private String id;
        private String name;
        private String city;
        private int age;

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }
        
    }
    
    static class Person2 extends Person{
        private int[] numbers;

        public int[] getNumbers() {
            return numbers;
        }

        public void setNumbers(int[] numbers) {
            this.numbers = numbers;
        }
        
    }

}
