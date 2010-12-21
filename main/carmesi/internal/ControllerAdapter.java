/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package carmesi.internal;

import carmesi.Controller;
import carmesi.ForwardTo;
import carmesi.RedirectTo;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Victor
 */
class ControllerAdapter implements  ControllerWrapper{
    private Controller controller;

    public ControllerAdapter(Controller controller) {
        this.controller = controller;
        
    }
    
    public Result execute(HttpServletRequest request, HttpServletResponse response) throws  Exception{
        controller.execute(request, response);
        return new Result() {

            public void process() {
                
            }

            public boolean isVoid() {
                return true;
            }

            public void writeToResponse(HttpServletResponse response) throws IOException {
                
            }
            
        };
    }

    public String getForwardTo() {
        ForwardTo forwardTo = controller.getClass().getAnnotation(ForwardTo.class);
        return forwardTo != null? forwardTo.value() : null;
    }

    public String getRedirectTo() {
        RedirectTo redirectTo = controller.getClass().getAnnotation(RedirectTo.class);
        return redirectTo != null? redirectTo.value() : null;
    }

}
