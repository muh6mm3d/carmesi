/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package carmesi.umbrella;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Victor
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface DatePattern {

    String value();
    
}
