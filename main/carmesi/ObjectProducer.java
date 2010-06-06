/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package carmesi;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This component is like a controller with a result that is an object. This object is send to the client in some kind of representation.
 * Main use is returning JSON objects.
 *
 * @author Victor
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
