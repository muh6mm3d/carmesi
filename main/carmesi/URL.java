/**
 * Insert license here.
 */

package carmesi;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates that a controller is going to serve the response when the specified url is requested.
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
    
}
