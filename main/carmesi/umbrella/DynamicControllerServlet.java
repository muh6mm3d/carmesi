/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package carmesi.umbrella;

import carmesi.umbrella.ControllerWrapper.Result;
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
//    private DynamicController controller;
    private ControllerWrapper wrapper=null;

    public DynamicControllerServlet(ControllerWrapper wrapper){
        this.wrapper=wrapper;
    }
    
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Result result = wrapper.execute(request, response);
            if(!result.isVoid()){
                result.writeToResponse(response);
            }else{
                result.process();
                if(wrapper.getForwardTo() != null){
                    request.getRequestDispatcher(wrapper.getForwardTo()).forward(request, response);
                }else if(wrapper.getRedirectTo() != null){
                    String url=wrapper.getRedirectTo();
                    if(url.startsWith("//")){
                        url=url.substring(2);
                    }else if(url.startsWith("/")){
                        url=getServletContext().getContextPath()+url;
                    }
                    response.sendRedirect(url);
                }
            }
        } catch (IllegalAccessException ex) {
            throw new ServletException(ex);
        } catch (InvocationTargetException ex) {
            throw new ServletException(ex);
        } catch (InstantiationException ex) {
            throw new ServletException(ex);
        } catch (IntrospectionException ex) {
            throw new ServletException(ex);
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

}
