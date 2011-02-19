package carmesi.internal.dynamic;

import carmesi.HttpMethod;
import carmesi.internal.AbstractControllerServlet;

/**
 * Supports dynamic controllers.
 * 
 * @author Victor Hugo Herrera Maldonado
 */
public class DynamicControllerServlet extends AbstractControllerServlet{

    public DynamicControllerServlet(DynamicController dynamicController) {
        super(dynamicController);
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
