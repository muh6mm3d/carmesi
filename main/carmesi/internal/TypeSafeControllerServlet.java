/*
 */

package carmesi.internal;

import carmesi.Controller;
import carmesi.ForwardTo;
import carmesi.HttpMethod;
import carmesi.RedirectTo;
import carmesi.URL;

/**
 *
 * @author Victor
 */
final class TypeSafeControllerServlet extends AbstractControllerServlet{
    private Controller controller;

    public TypeSafeControllerServlet(Controller controller) {
        super(controller);
        this.controller=controller;
    }
    
    public String getViewToForward() {
        ForwardTo forwardTo = controller.getClass().getAnnotation(ForwardTo.class);
        return forwardTo != null? forwardTo.value() : null;
    }

    public String getViewToRedirect() {
        RedirectTo redirectTo = controller.getClass().getAnnotation(RedirectTo.class);
        return redirectTo != null? redirectTo.value() : null;
    }

    public HttpMethod[] getValidMethods() {
        URL url=controller.getClass().getAnnotation(URL.class);
        return url != null ? HttpMethod.values() : (url.httpMethods().length == 0 ? HttpMethod.values(): url.httpMethods());
    }

}
