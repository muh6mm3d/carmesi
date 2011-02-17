/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package carmesi.internal;

import carmesi.HttpMethod;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Victor
 */
interface ControllerWrapper {
    
    Result execute(HttpServletRequest request, HttpServletResponse response) throws Exception;
    
    String getForwardTo();
    
    String getRedirectTo();
    
    HttpMethod[] getHttpMethods();
    
    public static interface  Result{
        
        void process();
        
        boolean isVoid();
        
        void writeToResponse(HttpServletResponse response) throws IOException;
        
    }

}
