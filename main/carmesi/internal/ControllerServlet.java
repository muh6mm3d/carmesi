/* Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0) */

package carmesi.internal;

import carmesi.Controller;
import carmesi.HttpMethod;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Base Servlet class for controllers annotated with URL. Uses Template Design Pattern.
 * 
 * @author Victor Hugo Herrera Maldonado
 */
class ControllerServlet extends HttpServlet{
    private Controller controller;
    private HttpMethod[] validHttpMethods=HttpMethod.values();
    private AfterControllerAction afterControllerAction;

    public ControllerServlet(Controller controller) {
        this.controller = controller;
    }

    public AfterControllerAction getAfterControllerAction() {
        return afterControllerAction;
    }

    public void setAfterControllerAction(AfterControllerAction afterControllerAction) {
        this.afterControllerAction = afterControllerAction;
    }

    public HttpMethod[] getValidHttpMethods() {
        return validHttpMethods;
    }

    public void setValidHttpMethods(HttpMethod[] validHttpMethods) {
        this.validHttpMethods = validHttpMethods;
    }
    
    public Controller getController() {
        return controller;
    }
    
    private boolean isValidHttpMethod(String httpMethod) throws ServletException{
        assert httpMethod != null;
        boolean validHttpMethod=false;
        for(HttpMethod method:validHttpMethods){
            if(httpMethod.equalsIgnoreCase(method.toString())){
                validHttpMethod=true;
                break;
            }
        }
        return validHttpMethod;
    }
    
    @Override
    protected final void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        assert request != null;
        assert response != null;
        try {
            if(!isValidHttpMethod(request.getMethod())){
                response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                return;
            }
            controller.execute(request, response);
            if(afterControllerAction != null){
                afterControllerAction.execute(request, response);
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
    
    static interface AfterControllerAction{
        
        void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException ; 
        
    }
    
    static class RedirectAction implements AfterControllerAction{
        private String url;

        public RedirectAction(String url) {
            this.url = url;
        }
        
        public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException {
            String tempUrl=url;
            if(tempUrl.startsWith("//")){
                tempUrl=tempUrl.substring(2);
            }else if(tempUrl.startsWith("/")){
                tempUrl=request.getContextPath()+tempUrl;
            }
            response.sendRedirect(tempUrl);
        }
        
    }
    
    static class ForwardAction implements AfterControllerAction{
        private String url;

        public ForwardAction(String url) {
            this.url = url;
        }
        
        public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            request.getRequestDispatcher(url).forward(request, response);
        }
        
    }

}
