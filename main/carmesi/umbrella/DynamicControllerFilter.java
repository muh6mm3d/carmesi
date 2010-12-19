/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package carmesi.umbrella;

import carmesi.umbrella.ControllerWrapper.Result;
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
    private ControllerWrapper controllerWrapper;

    public DynamicControllerFilter(ControllerWrapper controller){
        controllerWrapper=controller;
    }
    
    public void init(FilterConfig filterConfig) throws ServletException {
        
    }
    
    public void destroy() {
        
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            Result result = controllerWrapper.execute((HttpServletRequest)request, (HttpServletResponse)response);
            result.process();
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
