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
    private Map<String, Controller> mapControllers=new HashMap<String, Controller>();

    public void addController(String url, Controller controller){
        if(url == null || url.trim().equals("")){
            throw new IllegalArgumentException("url is empty");
        }
        mapControllers.put(url, controller);
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri=request.getRequestURI();
        uri=uri.replace(request.getServletContext().getContextPath(), "");
        if(mapControllers.containsKey(uri)){
            try{
                Controller object = mapControllers.get(uri);
                object.execute(request, response);
                if(object.getClass().isAnnotationPresent(RedirectTo.class)){
                    RedirectTo redirectToView=object.getClass().getAnnotation(RedirectTo.class);
                    response.sendRedirect(redirectToView.value());
                }else if(object.getClass().isAnnotationPresent(ForwardTo.class)){
                    ForwardTo forwardToView=object.getClass().getAnnotation(ForwardTo.class);
                    request.getRequestDispatcher(forwardToView.value()).forward(request, response);
                }
            }catch(Exception ex){
                throw new ServletException(ex);
            }
        }
    }
    
}
