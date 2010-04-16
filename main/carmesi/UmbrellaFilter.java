/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package umbrella;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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

public class UmbrellaFilter implements Filter {
    private Map<String, Controller> mapControllers=new HashMap<String, Controller>();

    public void init(FilterConfig filterConfig) throws ServletException {
        
    }

    public void addController(String url, Controller controller){
        mapControllers.put(url, controller);
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest hRequest=(HttpServletRequest)request;
        String uri=hRequest.getRequestURI();
        uri=uri.replace(hRequest.getServletContext().getContextPath(), "");
        System.out.println("requested uri: "+uri);
        if(mapControllers.containsKey(uri)){
            try{
                mapControllers.get(uri).execute(hRequest, (HttpServletResponse)response);
            }catch(Exception ex){
                throw new ServletException(ex);
            }
        }
        chain.doFilter(request, response);
    }

    public void destroy() {

    }

}
