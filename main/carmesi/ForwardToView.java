/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package carmesi;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation indicates that when the execution of the controller is finished, a forward must be made to the the view specified by <code>value</code>.
 *
 * @author Victor
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ForwardToView {
    /**
     *  The view to forward.
     */
     String value();
}
