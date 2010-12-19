/**
 * Insert license here.
 */

package carmesi.internal;

import carmesi.Controller;
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
 * To handle controller objects annotated with BeforeView. The filter invokes the associated Controller before invoking the next filter in the chain.
 *
 * @author Victor Hugo Herrera Maldonado
 */

public class CarmesiFilter implements Filter {
    private Controller controller;

    public CarmesiFilter(Controller controller) {
        this.controller = controller;
    }
    
    public void init(FilterConfig filterConfig) throws ServletException {
        
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try{
            controller.execute((HttpServletRequest)request, (HttpServletResponse)response);
            chain.doFilter(request, response);
        }catch(Exception ex){
            throw new ServletException(ex);
        }
    }

    public void destroy() {

    }

}
