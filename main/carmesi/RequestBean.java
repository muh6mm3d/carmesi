package carmesi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation to fill a bean with the parameters of the request. For each parameter name, a property with that name in the bean (if it's found) is set with the parameter value.
 * 
 * @author Victor
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface RequestBean {

}
