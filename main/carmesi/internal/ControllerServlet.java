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
    private HttpMethod[] validHttpMethods;
    private String forwardValue;
    private String redirectValue;
    
    static ControllerServlet createInstanceWithForward(Controller controller, String forwardValue, HttpMethod[] validHttpMethods){
        assert controller != null;
        assert forwardValue != null;
        assert validHttpMethods != null;
        ControllerServlet servlet=new ControllerServlet();
        servlet.controller=controller;
        servlet.forwardValue=forwardValue;
        servlet.validHttpMethods=validHttpMethods;
        return servlet;
    }
    
    static ControllerServlet createInstanceWithRedirect(Controller controller, String redirectValue, HttpMethod[] validHttpMethods){
        assert controller != null;
        assert redirectValue != null;
        assert validHttpMethods != null;
        ControllerServlet servlet=new ControllerServlet();
        servlet.controller=controller;
        servlet.redirectValue=redirectValue;
        servlet.validHttpMethods=validHttpMethods;
        return servlet;
    }

    public Controller getController() {
        return controller;
    }
    
    private void validateHttpMethod(HttpServletRequest request) throws ServletException{
        assert request != null;
        boolean validHttpMethod=false;
        for(HttpMethod method:validHttpMethods){
            if(request.getMethod().equals(method.toString())){
                validHttpMethod=true;
                break;
            }
        }
        if(!validHttpMethod){
            throw new ServletException("Not valid HTTP method: "+request.getMethod());
        }
    }
    
    private void toView(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        assert request != null;
        assert response != null;
        if(forwardValue != null){
            request.getRequestDispatcher(forwardValue).forward(request, response);
        }else if(redirectValue != null){
            String url=redirectValue;
            if(url.startsWith("//")){
                url=url.substring(2);
            }else if(url.startsWith("/")){
                url=request.getContextPath()+url;
            }
            response.sendRedirect(url);
        }
    }
    
    protected final void executeController(HttpServletRequest request, HttpServletResponse response) throws Exception{
        assert request != null;
        assert response != null;
        controller.execute(request, response);
    }
    
    @Override
    protected final void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        assert request != null;
        assert response != null;
        try {
            validateHttpMethod(request);
            executeController(request, response);
            toView(request, response);
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
