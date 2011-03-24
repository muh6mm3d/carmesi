/* Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0) */

package carmesi.json;

/**
 * Serialize an object to its JSON representation. The implementations area free of using any JSON library.
 * 
 * @author Victor Hugo Herrera Maldonado
 */
public interface JSONSerializer {
    
    /**
     * Serializes the object to JSON.
     * 
     * @param object to serialize
     * @return The JSON representation of the object.
     */
    String serialize(Object object);
 
}
