/**
 * Insert license here.
 */

package carmesi;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates that a controller is going to execute the response when the specified url is requested.
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
    
    HttpMethod[] httpMethods() default {};
    
}
