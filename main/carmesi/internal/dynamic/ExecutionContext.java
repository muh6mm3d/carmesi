package carmesi.internal.dynamic;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Contains the request, response and servlet context to be handled as a Value Object.
 * 
 * @author Victor
 */
public class ExecutionContext {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ServletContext servletContext;

    public ExecutionContext(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        this.servletContext = request.getServletContext();
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }
    
}
