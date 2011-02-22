/* Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0) */

package carmesi.internal.simplecontrollers;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Simple bean containing the request, response and servlet context.
 * 
 * @author Victor Hugo Herrera Maldonado
 */
class ExecutionContext {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ServletContext servletContext;

    ExecutionContext(HttpServletRequest request, HttpServletResponse response) {
        assert request != null;
        assert response != null;
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
