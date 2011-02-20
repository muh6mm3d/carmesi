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
    
    protected final String getViewToForward() {
        return ((DynamicController)getController()).getForwardTo();
    }

    protected final String getViewToRedirect() {
        return ((DynamicController)getController()).getRedirectTo();
    }

    protected final HttpMethod[] getValidMethods() {
        return ((DynamicController)getController()).getHttpMethods();
    }
    
}
