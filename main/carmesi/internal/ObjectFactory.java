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
