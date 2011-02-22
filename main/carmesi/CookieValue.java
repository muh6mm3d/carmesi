/* Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0) */

package carmesi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represent a Cookie. If used in method parameters, that parameter will be injected from the cookie.
 * If used in method, the return value is converted to String and used to add a Cookie to the response.
 *
 * @author Victor Hugo Herrera Maldonado
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.METHOD})
public @interface CookieValue {
    
    /**
     * Name of the cookie
     * @return String
     */
    String value();

}
