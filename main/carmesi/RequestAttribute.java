/* Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0) */

package carmesi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Represents an attribute in the request. If used in method parameter, the value of parameter is retrieved from the request.
 * If used in method, the return value of the method is set in the request.
 * </p>
 * @author Victor Hugo Herrera Maldonado
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.PARAMETER})
public @interface RequestAttribute {
    
    /**
     * Name of the attribute.
     * 
     * @return String.
     */
    String value();

}
