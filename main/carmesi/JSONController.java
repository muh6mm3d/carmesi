/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package carmesi;

import com.google.gson.Gson;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Victor
 */
public abstract class JSONController<T> implements Controller{

    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        T t=get(request, response);
        Gson gson=new Gson();
        String json = gson.toJson(t);
        response.getWriter().write(json);
        response.getWriter().close();
    }

    public abstract T get(HttpServletRequest request, HttpServletResponse response) throws Exception;

}
