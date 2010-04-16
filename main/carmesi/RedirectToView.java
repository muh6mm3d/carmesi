/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package umbrella;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * @author Victor
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RedirectToView {
    String value();
}
