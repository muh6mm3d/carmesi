/**
 * Insert license here.
 */

package carmesi;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A type safe interface for making controller classes.
 *
 * @author Victor Hugo Herrera Maldonado
 */
public interface Controller {

    /**
     * Executes the controller.
     *
     * @param request The servlet request
     * @param response The servlet response
     * @throws Exception if there is a failure.
     */
    void execute(HttpServletRequest request, HttpServletResponse response) throws Exception;

}
