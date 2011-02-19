package carmesi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Represents an attribute in the HttpSession. If used in parameter, the value of parameter is retrieved from the HttpSession.
 * If used in method, the return value of the method is set in the HttpSession.
 * </p>
 * @author Victor Hugo Herrera Maldonado
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.PARAMETER})
public @interface SessionAttribute {
 
    /**
     * Name of the attribute
     * 
     * @return String.
     */
    String value();

}
