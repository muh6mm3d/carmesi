/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package carmesi.internal;

import carmesi.HttpMethod;
import carmesi.internal.ControllerWrapper.Result;
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
class ControllerServlet extends  HttpServlet{
    private ControllerWrapper wrapper=null;

    public ControllerServlet(ControllerWrapper wrapper){
        this.wrapper=wrapper;
    }
    
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            boolean validHttpMethod=false;
            for(HttpMethod method:wrapper.getHttpMethods()){
                if(request.getMethod().equals(method.toString())){
                    validHttpMethod=true;
                    break;
                }
            }
            if(validHttpMethod){
                Result result = wrapper.execute(request, response);
                if(wrapper.getForwardTo() == null && wrapper.getRedirectTo() == null && !result.isVoid()){
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
                            url=request.getContextPath()+url;
                        }
                        response.sendRedirect(url);
                    }
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
