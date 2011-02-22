/* Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0) */

package carmesi.internal;

import carmesi.Controller;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Filter used for controller marked with BeforeURL. This filter first invoke the controller and later the execution is passed to the filter chain.
 *
 * @author Victor Hugo Herrera Maldonado
 */
class ControllerFilter implements  Filter{
    private Controller controller;

    ControllerFilter(Controller controller) {
        assert controller != null;
        this.controller = controller;
    }
    
    public Controller getController(){
        return controller;
    }
    
    public void init(FilterConfig filterConfig) throws ServletException {
        
    }
    
    public void destroy() {
        
    }
    
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        assert request != null;
        assert response != null;
        assert chain != null;
        try {
            controller.execute((HttpServletRequest)request, (HttpServletResponse)response);
            chain.doFilter(request, response);
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
