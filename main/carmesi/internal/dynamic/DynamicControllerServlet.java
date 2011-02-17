/*
 */

package carmesi.internal.dynamic;

import carmesi.HttpMethod;
import carmesi.internal.AbstractControllerServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Victor
 */
public class DynamicControllerServlet extends AbstractControllerServlet{

    public DynamicControllerServlet(Object controller) {
        super(DynamicController.createDynamicController(controller));
    }
    
    public String getViewToForward() {
        return ((DynamicController)getController()).getForwardTo();
    }

    public String getViewToRedirect() {
        return ((DynamicController)getController()).getRedirectTo();
    }

    public HttpMethod[] getValidMethods() {
        return ((DynamicController)getController()).getHttpMethods();
    }
    
}
