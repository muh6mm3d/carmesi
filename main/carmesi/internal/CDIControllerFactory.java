package carmesi.internal;

import java.util.Collection;
import java.util.LinkedList;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * ControllerFactory implementation with support of CDI.
 * 
 * @author Victor Hugo Herrera Maldonado
 */
class CDIControllerFactory implements ControllerFactory{
    private BeanManager beanManager;
    private CreationalContext creationalContext;
    private Collection<Object> objects=new LinkedList<Object>();

    public void init() {
        try {
            InitialContext initialContext=new InitialContext();
            beanManager=(BeanManager) initialContext.lookup("java:comp/BeanManager");
            creationalContext=beanManager.createCreationalContext(null);
        } catch (NamingException ex) {
            ex.printStackTrace();
            throw new AssertionError(ex.toString());
        }
    }

    public void dispose() {
        for(Object o:objects){
            if(o instanceof InjectionTarget){
                InjectionTarget target=(InjectionTarget) o;
                target.preDestroy(o);
                target.dispose(o);
            }
        }
        if(creationalContext != null){
            creationalContext.release();
        }
    }

    public <T> T createController(Class<T> klass) throws InstantiationException, IllegalAccessException {
        T object;
        AnnotatedType<T> annotatedType = beanManager.createAnnotatedType(klass);
        InjectionTarget<T> target = beanManager.createInjectionTarget(annotatedType);
        if(creationalContext == null){
            creationalContext = beanManager.createCreationalContext(null);
        }
        object = target.produce(creationalContext);
        target.inject(object, creationalContext);
        target.postConstruct(object);
        objects.add(target);
        return object;
    }
    
}
