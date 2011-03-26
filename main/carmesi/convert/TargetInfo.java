/* Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0) */

package carmesi.convert;

import java.lang.annotation.Annotation;

/**
 * Contains the type and annotations of the value to inject. Target can be either a parameter of a method or a property of a bean.
 * 
 * @author Victor Hugo Herrera Maldonado
 */
public class TargetInfo {
    private Class parameterType;
    private Annotation[] annotations;

    /**
     * Creates the instance.
     * 
     * @param parameterType
     * @param annotations 
     * @throws NullPointerException if parameterType or annotations is null
     */
    public TargetInfo(Class parameterType, Annotation[] annotations) {
        if (parameterType == null) {
            throw new NullPointerException("parameterType is null");
        }
        if (annotations == null) {
            throw new NullPointerException("annotations is null");
        }
        this.parameterType = parameterType;
        this.annotations = annotations;
    }

    /**
     * Get the annotations of the target.
     * 
     * @return Annotation[]
     */
    public Annotation[] getAnnotations() {
        return annotations;
    }
    
    /**
     * Get the type of the target.
     * 
     * @return Class
     */
    public Class getType() {
        return parameterType;
    }
    
    /**
     * Indicates if the target contains an annotation of the given type.
     * 
     * @param <A>
     * @param annotationClass
     * @return 
     */
    public <A extends Annotation> boolean isAnnotationPresent(Class<A> annotationClass){
        for(Annotation a: annotations){
            if(a.annotationType().equals(annotationClass)){
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get the annotation with type in the target if exists.
     * 
     * @param <A>
     * @param annotationClass Type of the annotation
     * @return Annotation
     */
    @SuppressWarnings("unchecked")
    public <A extends Annotation> A getAnnotation(Class<A> annotationClass){
        for(Annotation a: annotations){
            if(a.annotationType().equals(annotationClass)){
                return (A) a;
            }
        }
        return null;
    }

}
