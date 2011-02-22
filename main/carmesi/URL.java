/* Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0) */

package carmesi;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates that a controller is going to execute the controller when the specified url is requested.
 *
 * @author Victor Hugo Herrera Maldonado
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface URL {
    
    /**
     * The string url to execute the controller.
     * 
     * @return
     */
    String value();
    
    /**
     * The valid http methods for invocating a controller.
     * @return 
     */
    HttpMethod[] httpMethods() default {};
    
}
