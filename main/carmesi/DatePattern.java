/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package carmesi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents a pattern for the conversion of String object to a Date object.
 * 
 * @author Victor
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface DatePattern {

    /**
     * @return  The date pattern.
     */
    String value();
    
}
