/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package carmesi.umbrella;

import carmesi.ForwardTo;
import carmesi.RedirectTo;
import carmesi.umbrella.ControllerWrapper.Result;
import carmesi.umbrella.DynamicController.ControllerResult;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Victor
 */
public class DynamicControllerServlet extends  HttpServlet{
    private DynamicController controller;

    public DynamicControllerServlet(Object o){
        controller = DynamicController.createDynamicController(o);
    }
    
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
//            ControllerWrapper wrapper=null;
//            Result r = wrapper.execute(request, response);
//            if(!r.isVoid()){
//                r.writeToResponse(response);
//            }else{
//                r.process();
//                if(wrapper.getForwardTo() != null){
//                    request.getRequestDispatcher(wrapper.getForwardTo()).forward(request, response);
//                }else if(wrapper.getRedirectTo() != null){
//                    String url=wrapper.getRedirectTo();
//                    if(url.startsWith("//")){
//                        url=url.substring(2);
//                    }else if(url.startsWith("/")){
//                        url=getServletContext().getContextPath()+url;
//                    }
//                    response.sendRedirect(url);
//                }
//            }
            
            
            ExecutionContext executionContext = new ExecutionContext(request, response);
            ControllerResult result = controller.execute(executionContext);
            ForwardTo forwardTo=controller.getObject().getClass().getAnnotation(ForwardTo.class);
            RedirectTo redirectTo=controller.getObject().getClass().getAnnotation(RedirectTo.class);
            if(forwardTo != null){
                result.process(executionContext);
                request.getRequestDispatcher(forwardTo.value()).forward(request, response);
            }else if(redirectTo != null){
                result.process(executionContext);
                String url=redirectTo.value();
                if(url.startsWith("//")){
                    url=url.substring(2);
                }else if(url.startsWith("/")){
                    url=getServletContext().getContextPath()+url;
                }
                response.sendRedirect(url);
            }else{
                result.writeToResponse(response);
            }
        } catch (IllegalAccessException ex) {
            throw new ServletException(ex);
        } catch (InvocationTargetException ex) {
            throw new ServletException(ex);
        } catch (InstantiationException ex) {
            throw new ServletException(ex);
        } catch (IntrospectionException ex) {
            throw new ServletException(ex);
        }
    }

}
