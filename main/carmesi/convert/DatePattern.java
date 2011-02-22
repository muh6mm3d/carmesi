/* Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0) */

package carmesi.convert;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents a pattern for the conversion of String object to a Date object (Example: dd/MM/yyyy).
 * 
 * @author Victor Hugo Herrera Maldonado
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD})
public @interface DatePattern {

    /**
     * The date pattern.
     * 
     * @return String.
     */
    String value();
    
}
