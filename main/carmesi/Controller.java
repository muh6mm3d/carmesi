/**
 * Insert license here.
 */

package carmesi;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Based on the MVC pattern, a controller has the function of being the link between the component that the user interacts with and the bussiness logic.
 *
 * @author Victor Hugo Herrera Maldonado
 */
public interface Controller {

    /**
     * Executes the controller.
     *
     * @param request
     * @param response
     * @throws Exception if there is a failure.
     */
    void execute(HttpServletRequest request, HttpServletResponse response) throws Exception;

}
