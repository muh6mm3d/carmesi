package carmesi.jsonserializers;

import java.io.IOException;
import java.io.StringWriter;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Serialize an object to its JSON representation using Jackson library. Be sure of adding the Jackson library to your project.
 *
 * @author Victor Hugo Herrera Maldonado
 */
public class JacksonSerializer implements JSONSerializer{

    public String serialize(Object object) {
        try {
            StringWriter writer=new StringWriter();
            new ObjectMapper().writeValue(writer, object);
            return writer.toString();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } 
    }

}
