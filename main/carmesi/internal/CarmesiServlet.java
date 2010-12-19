/**
 * Insert license here.
 */
package carmesi.internal;

import carmesi.Controller;
import carmesi.ForwardTo;
import carmesi.RedirectTo;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles the controller objects annotated with ForwardToView or RedirectToView. Invokes the controller and if the invocation is successful (ie, without throwing exceptions)
 * makes a forward or a redirect (accordly to the type of annotation) to the appropiate view.
 *
 *
 * @author Victor Hugo Herrera Maldonado
 */
public class CarmesiServlet extends HttpServlet {
    private Controller controller;

    public CarmesiServlet(Controller controller) {
        this.controller = controller;
    }
    
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{
            controller.execute(request, response);
            if(controller.getClass().isAnnotationPresent(RedirectTo.class)){
                RedirectTo redirectTo=controller.getClass().getAnnotation(RedirectTo.class);
                String url=redirectTo.value();
                if(url.startsWith("//")){
                    url=url.substring(2);
                }else if(url.startsWith("/")){
                    url=getServletContext().getContextPath()+url;
                }
                response.sendRedirect(url);
            }else if(controller.getClass().isAnnotationPresent(ForwardTo.class)){
                ForwardTo forwardTo=controller.getClass().getAnnotation(ForwardTo.class);
                request.getRequestDispatcher(forwardTo.value()).forward(request, response);
            }
        }catch(Exception ex){
            throw new ServletException(ex);
        }
    }
    
}
