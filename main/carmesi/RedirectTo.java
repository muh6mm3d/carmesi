package carmesi;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation indicates that when the execution of the controller is finished, a redirect must be made to the the specified url.
 * 
 * If value starts with one slash, the Context path is added. 
 *
 * @author Victor Hugo Herrera Maldonado
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RedirectTo {
    
    /**
     * The url to redirect after the controller is executed.
     * 
     * return String.
     */
    String value();
    
}
