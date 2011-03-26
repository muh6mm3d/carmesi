/* Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0) */

package carmesi.internal;

import java.util.Collection;
import java.util.LinkedList;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * ObjectFactory implementation with support of CDI.
 * 
 * @author Victor Hugo Herrera Maldonado
 */
class CDIObjectFactory implements ObjectFactory{
    private BeanManager beanManager;
    private Collection<ObjectWrapper<?>> objects=new LinkedList<ObjectWrapper<?>>();

    public void init() {
        try {
            InitialContext initialContext=new InitialContext();
            beanManager=(BeanManager) initialContext.lookup("java:comp/BeanManager");
        } catch (NamingException ex) {
            ex.printStackTrace();
            throw new AssertionError(ex.toString());
        }
    }

    @SuppressWarnings("unchecked")
    public void dispose() {
        for(ObjectWrapper<?> wrapper:objects){
            ((InjectionTarget<Object>)wrapper.getInjectionTarget()).preDestroy(wrapper.getObject());
            ((InjectionTarget<Object>)wrapper.getInjectionTarget()).dispose(wrapper.getObject());
            wrapper.getCreationalContext().release();
        }
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
        T object;
        AnnotatedType<T> annotatedType = beanManager.createAnnotatedType(klass);
        InjectionTarget<T> target = beanManager.createInjectionTarget(annotatedType);
        CreationalContext<T> creationalContext = beanManager.<T>createCreationalContext(null);
        object = target.produce(creationalContext);
        target.inject(object, creationalContext);
        target.postConstruct(object);
        objects.add(new ObjectWrapper<T>(object, target, creationalContext));
        return object;
    }
    
    private class ObjectWrapper<T>{
        private T object;
        private InjectionTarget<T> injectionTarget;
        private CreationalContext<T> creationalContext;

        private ObjectWrapper(T object, InjectionTarget<T> injectionTarget, CreationalContext<T> context) {
            assert object != null;
            assert injectionTarget != null;
            assert context != null;
            this.object = object;
            this.injectionTarget = injectionTarget;
            this.creationalContext=context;
        }

        private InjectionTarget<T> getInjectionTarget() {
            return injectionTarget;
        }

        private T getObject() {
            return object;
        }

        public CreationalContext<T> getCreationalContext() {
            return creationalContext;
        }
        
    }
    
}
