/* Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0) */

package carmesi.convert;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used in a converter to indicate that the converter is used for a target type.
 * 
 * @author Victor Hugo Herrera Maldonado
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ConverterFor {
    
    /**
     * The target type valid for the converter.
     * 
     * @return Type of the target.
     */
    Class value();

}
