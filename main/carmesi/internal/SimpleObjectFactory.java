package carmesi.internal;

/**
 * ObjectFactory implementation that just directly instance the class.
 *
 * @author Victor Hugo Herrera Maldonado
 */
class SimpleObjectFactory implements ObjectFactory{

    public void init() {
        
    }

    public void dispose() {
        
    }

    /**
     * Creates an instance of the given klass.
     * 
     * @param <T>
     * @param klass
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws NullPointerException if klass is null
     */
    public <T> T createController(Class<T> klass) throws NullPointerException, InstantiationException, IllegalAccessException {
        if (klass == null) {
            throw new NullPointerException("klass is null");
        }
        return klass.newInstance();
    }

}
