package carmesi.internal;

/**
 * ControllerFactory implementation that just directly instance the class.
 *
 * @author Victor Hugo Herrera Maldonado
 */
class SimpleControllerFactory implements ControllerFactory{

    public void init() {
        
    }

    public void dispose() {
        
    }

    public <T> T createController(Class<T> klass) throws InstantiationException, IllegalAccessException {
        return klass.newInstance();
    }

}
