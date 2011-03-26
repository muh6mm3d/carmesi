/* Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0) */

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
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BeforeURL {
    /**
     * The controller is executed before the resource specified by value is served.
     * 
     * @return url
     */
    String value();
}
