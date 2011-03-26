/* Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0) */

package carmesi;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates that a controller is going to be executed when the specified url is requested.
 *
 * @author Victor Hugo Herrera Maldonado
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface URL {
    
    /**
     * The string url to execute the controller.
     * 
     * @return The url for the controller.
     */
    String value();
    
}
