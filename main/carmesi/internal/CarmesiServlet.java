/**
 * Insert license here.
 */
package carmesi.internal;

import carmesi.Controller;
import carmesi.ForwardToView;
import carmesi.ObjectProducer;
import carmesi.RedirectToView;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles the controller objects annotated with ForwardToView or RedirectToView. Invokes the controller and if the invocation is successful (ie, without throwing exceptions)
 * makes a forward or a redirect (accordly to the type of annotation) to the appropiate view.
 *
 *
 * @author Victor Hugo Herrera Maldonado
 */
public class CarmesiServlet extends HttpServlet {
    private Map<String, Object> mapControllers=new HashMap<String, Object>();

    public void addController(String url, Controller controller){
        if(url == null || url.trim().equals("")){
            throw new IllegalArgumentException("url is empty");
        }
        mapControllers.put(url, controller);
    }

    public void addObjectProducer(String url, ObjectProducer producer){
        if(url == null || url.trim().equals("")){
            throw new IllegalArgumentException("url is empty");
        }
        mapControllers.put(url, producer);
    }
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String uri=request.getRequestURI();
        uri=uri.replace(request.getServletContext().getContextPath(), "");
        if(mapControllers.containsKey(uri)){
            try{
                Object object = mapControllers.get(uri);
                if(object instanceof ObjectProducer){
                    Object o = ((ObjectProducer) object).get(request, response);
                    String json=new GsonBuilder().create().toJson(o);
                    response.getWriter().print(json);
                    response.getWriter().close();
                }
                if(!(object instanceof ObjectProducer)){
                    if(object.getClass().isAnnotationPresent(RedirectToView.class)){
                        RedirectToView redirectToView=object.getClass().getAnnotation(RedirectToView.class);
                        response.sendRedirect(redirectToView.value());
                    }else if(object.getClass().isAnnotationPresent(ForwardToView.class)){
                        ForwardToView forwardToView=object.getClass().getAnnotation(ForwardToView.class);
                        request.getRequestDispatcher(forwardToView.value()).forward(request, response);
                    }
                }
            }catch(Exception ex){
                throw new ServletException(ex);
            }
        }
    } 

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
