/* Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0) */

package carmesi;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation indicates that when the execution of the controller is finished, a forward must be made to the the specified url.
 *
 * @author Victor Hugo Herrera Maldonado
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ForwardTo {
    
    /**
     * The url to forward after the controller is executed.
     * 
     * return String.
     */
     String value();
}
