/* Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0) */
package carmesi.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * ObjectFactory implementation that just directly instance the class.
 *
 * @author Victor Hugo Herrera Maldonado
 */
class SimpleObjectFactory implements ObjectFactory {

    private List<CallbackWrapper> callbackWrappers = new LinkedList<CallbackWrapper>();

    public void init() {
    }

    public void dispose() {
        for (CallbackWrapper wrapper : callbackWrappers) {
            wrapper.callPreDestroy();
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
        T instance = klass.newInstance();
        CallbackWrapper wrapper = new CallbackWrapper(instance);
        callbackWrappers.add(wrapper);
        wrapper.callPostConstruct();
        return instance;
    }

    private static Method getCallbackMethod(Class klass, Class<? extends Annotation> annotationCallback) {
        assert klass != null;
        assert annotationCallback != null;
        Method callbackMethod = null;
        for (Method method : klass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotationCallback)) {
                if (callbackMethod != null) {
                    throw new IllegalArgumentException("Only one method annotated with " + annotationCallback.getSimpleName() + " is allowed.");
                }
                checkCallbackMethod(method, annotationCallback);
                callbackMethod = method;
            }
        }
        return callbackMethod;
    }

    private static void checkCallbackMethod(Method method, Class<? extends Annotation> annotationCallback) {
        if (Modifier.isStatic(method.getModifiers())) {
            throw new IllegalArgumentException(annotationCallback.getSimpleName() + " method must not be static.");
        }
        if (method.getParameterTypes().length > 0) {
            throw new IllegalArgumentException(annotationCallback.getSimpleName() + " method must not have parameters.");
        }
        if (!method.getReturnType().equals(Void.TYPE)) {
            throw new IllegalArgumentException(annotationCallback.getSimpleName() + " method must be void.");
        }
        for (Class<?> exClass : method.getExceptionTypes()) {
            if (!RuntimeException.class.isAssignableFrom(exClass)) {
                throw new IllegalArgumentException(annotationCallback.getSimpleName() + " method must not throw checked exceptions.");
            }
        }
    }

    private static class CallbackWrapper {

        private Object object;
        private List<Method> postConstructMethods = new LinkedList<Method>();
        private List<Method> preDestroyMethods = new LinkedList<Method>();

        public CallbackWrapper(Object o) {
            object = o;
            for (Class klass = o.getClass(); klass != null; klass = klass.getSuperclass()) {
                Method method;
                method = getCallbackMethod(klass, PostConstruct.class);
                if (method != null) {
                    postConstructMethods.add(method);
                }
                method = getCallbackMethod(klass, PreDestroy.class);
                if (method != null) {
                    preDestroyMethods.add(method);
                }
            }
        }

        public void callPostConstruct() {
            try{
                for(Method method:postConstructMethods){
                    invokeCallbackMethod(method);
                }
            } catch(IllegalAccessException ex){
                throw new AssertionError();
            } catch(InvocationTargetException ex){
                throw new RuntimeException(ex);
            }
        }

        public void callPreDestroy() {
            try{
                for(Method method:preDestroyMethods){
                    invokeCallbackMethod(method);
                }
            } catch(IllegalAccessException ex){
                throw new AssertionError();
            } catch(InvocationTargetException ex){
                throw new RuntimeException(ex);
            }
        }
        
        private void invokeCallbackMethod(Method callbackMethod) throws IllegalAccessException, InvocationTargetException{
            boolean tmp = callbackMethod.isAccessible();
            callbackMethod.setAccessible(true);
            callbackMethod.invoke(object);
            callbackMethod.setAccessible(tmp);
        }
        
    }
    
}
