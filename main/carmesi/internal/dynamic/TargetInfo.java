/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package carmesi.internal.dynamic;

import java.lang.annotation.Annotation;

/**
 * Contains the type and annotations of the value to inject. Target can be either a parameter of a method or a property of a bean.
 * 
 * @author Victor
 */
public class TargetInfo {
    private Class parameterType;
    private Annotation[] annotations;

    public TargetInfo(Class parameterType, Annotation[] annotations) {
        this.parameterType = parameterType;
        this.annotations = annotations;
    }

    public Annotation[] getAnnotations() {
        return annotations;
    }
    
    public Class getParameterType() {
        return parameterType;
    }
    
    public <A extends Annotation> boolean hasAnnotation(Class<A> annotationClass){
        for(Annotation a: annotations){
            if(a.annotationType().equals(annotationClass)){
                return true;
            }
        }
        return false;
    }
    
    public <A extends Annotation> A getAnnotation(Class<A> annotationClass){
        for(Annotation a: annotations){
            if(a.annotationType().equals(annotationClass)){
                return (A) a;
            }
        }
        return null;
    }

}
