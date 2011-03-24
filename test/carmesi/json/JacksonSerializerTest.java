/* Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0) */

package carmesi.json;

import java.io.IOException;
import java.io.StringReader;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author Victor Hugo Herrera Maldonado
 */
public class JacksonSerializerTest {

    @Test
    public void shouldBeSameObjectFromSerialization() throws IOException {
        JacksonSerializer serializer=new JacksonSerializer();
        B b1=new B("Victor", "Herrera", 29, new D("d"));
        String jsonString = serializer.serialize(b1);
        ObjectMapper mapper=new ObjectMapper();
        StringReader reader=new StringReader(jsonString);
        B b2=mapper.readValue(reader, B.class);
        assertThat(b2.getFirstName(), is(b1.getFirstName()));
        assertThat(b2.getLastName(), is(b1.getLastName()));
        assertThat(b2.getAge(), is(b1.getAge()));
        System.out.println("json: "+jsonString);
    }
    
    private static class B{
        private String firstName;
        private String lastName;
        private int age;
        private D d;

        public B() {
        }

        public B(String firstName, String lastName, int age, D d) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.age = age;
            this.d = d;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public D getD() {
            return d;
        }

        public void setD(D d) {
            this.d = d;
        }
        
    }
    
    private static class D{
        private String name;

        public D() {
        }

        public D(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
        
    }

}