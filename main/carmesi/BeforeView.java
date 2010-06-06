/**
 * Insert license here.
 */

package carmesi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * A controller with this annotation is executed before the specified response is served.
 *
 * @author Victor Hugo Herrera Maldonado
 *
 * TODO resolutions rules or precedence.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BeforeView {
    /**
     * The view for what the controller is executed before being served.
     */
    String value();
}
