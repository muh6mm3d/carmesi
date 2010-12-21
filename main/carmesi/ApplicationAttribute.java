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
 * <p>
 * Represents an attribute in ServletContext. If used in parameter, the value of parameter is retrieved from the ServletContext.
 * If used in method, the return value of the method is set in the ServletContext.
 * </p>
 * @author Victor
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.PARAMETER})
public @interface ApplicationAttribute {

    /**
     * @return Name of the attribute.
     */
    String value();
    
}
