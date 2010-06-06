/**
 * Insert license here.
 */

package carmesi;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This component is for generating objects and returning it to the browser in a JSON representation. The convertion from the java object to JSON
 * is made automatically by Carmesi.
 *
 * @author Victor Hugo Herrera Maldonado
 */
public interface ObjectProducer<T> {

    /**
     * Get an object response o throws an exception if there is a failure.
     *
     * @return An object response.
     * @throws Exception if there is a failure.
     */
    T get(HttpServletRequest request, HttpServletResponse response) throws Exception;

}
