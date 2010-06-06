/**
 * Insert license here.
 */

package carmesi;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation indicates that when the execution of the controller is finished, a redirect must be made to the the specified view.
 *
 * @author Victor Hugo Herrera Maldonado
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RedirectToView {
    /**
     * The view to redirect.
     */
    String value();
}
