/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package carmesi.internal;

import carmesi.internal.ControllerWrapper.Result;
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
 *
 * @author Victor
 */
class ControllerFilter implements  Filter{
    private ControllerWrapper controllerWrapper;

    public ControllerFilter(ControllerWrapper controller){
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
