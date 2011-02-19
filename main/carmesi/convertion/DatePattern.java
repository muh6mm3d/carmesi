package carmesi.convertion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents a pattern for the conversion of String object to a Date object.
 * 
 * @author Victor Hugo Herrera Maldonado
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface DatePattern {

    /**
     * The date pattern.
     * 
     * @return String.
     */
    String value();
    
}
