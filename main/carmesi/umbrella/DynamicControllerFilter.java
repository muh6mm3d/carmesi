/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package carmesi.umbrella;

import carmesi.umbrella.DynamicController.ControllerResult;
import carmesi.umbrella.ExecutionContext;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Victor
 */
public class DynamicControllerFilter implements  Filter{
    private DynamicController controller;

    public DynamicControllerFilter(Object o){
        controller=DynamicController.createDynamicController(o);
    }
    
    public void init(FilterConfig filterConfig) throws ServletException {
        
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            ExecutionContext executionContext = new ExecutionContext((HttpServletRequest)request, (HttpServletResponse)response);
            ControllerResult result;
            result = controller.execute(executionContext);
            result.process(executionContext);
        } catch (IllegalAccessException ex) {
            throw new ServletException(ex);
        } catch (InvocationTargetException ex) {
            throw new ServletException(ex);
        } catch (InstantiationException ex) {
            throw new ServletException(ex);
        } catch (IntrospectionException ex) {
            throw new ServletException(ex);
        }
        chain.doFilter(request, response);
    }

    public void destroy() {
        
    }

}
