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
 * Represents a value to be retrieved from a parameter of the request.
 * 
 * @author Victor
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface RequestParameter {
    
    /**
     * @return Name of the parameter.
     */
    String value();

}
