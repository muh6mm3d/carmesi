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
 * Base class for controllers. Uses Template Design Pattern.
 * 
 * @author Victor Hugo Herrera Maldonado
 */
public abstract class AbstractControllerServlet extends HttpServlet{
    private Controller controller;

    public AbstractControllerServlet(Controller controller) {
        this.controller = controller;
    }

    public Controller getController() {
        return controller;
    }
    
    public abstract HttpMethod[] getValidMethods();
    
    public abstract String getViewToForward();
    
    public abstract String getViewToRedirect();
    
    private void validateHttpMethod(HttpServletRequest request) throws ServletException{
        boolean validHttpMethod=false;
        for(HttpMethod method:getValidMethods()){
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
        if(getViewToForward() != null){
            request.getRequestDispatcher(getViewToForward()).forward(request, response);
        }else if(getViewToRedirect() != null){
            String url=getViewToRedirect();
            if(url.startsWith("//")){
                url=url.substring(2);
            }else if(url.startsWith("/")){
                url=request.getContextPath()+url;
            }
            response.sendRedirect(url);
        }
    }
    
    protected final void executeController(HttpServletRequest request, HttpServletResponse response) throws Exception{
        controller.execute(request, response);
    }
    
    @Override
    protected final void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
