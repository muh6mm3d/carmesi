/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package umbrella;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Victor
 */
public interface Controller {

    void execute(HttpServletRequest request, HttpServletResponse response) throws Exception;

}
