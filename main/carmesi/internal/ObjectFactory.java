/* Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0) */

package carmesi.internal;

/**
 * A factory of the controller objects. This is interface was used for allowing use of CDI only when is available.
 * 
 * @author Victor Hugo Herrera Maldonado
 */
interface ObjectFactory {
    
    void init();
    
    void dispose();
    
    <T> T createController(Class<T> klass) throws InstantiationException, IllegalAccessException ;

}
