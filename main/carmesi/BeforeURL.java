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
 * A controller with this annotation is executed before the resource specified by the url value is served.
 *
 * @author Victor Hugo Herrera Maldonado
 *
 * TODO resolutions rules or precedence.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BeforeURL {
    /**
     * The controller is executed the resource specified by value is served.
     */
    String value();
}
