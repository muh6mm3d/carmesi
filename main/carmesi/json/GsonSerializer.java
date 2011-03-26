/* Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0) */

package carmesi.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Serialize an object to its JSON representation using Gson library. Be sure of adding the Gson library to your project.
 * <p>
 * This serializer internally uses GsonBuilder without custom configuration.
 *
 * @author Victor Hugo Herrera Maldonado
 */
public class GsonSerializer implements  JSONSerializer{
    private GsonBuilder builder=new GsonBuilder();

    public String serialize(Object object) {
        Gson gson = builder.create();
        return gson.toJson(object);
    }

}
