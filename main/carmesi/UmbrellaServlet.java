/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package umbrella;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Victor
 */
public class UmbrellaServlet extends HttpServlet {
    private Map<String, Controller> mapControllers=new HashMap<String, Controller>();

    public void addController(String url, Controller controller){
        if(url == null || url.trim().equals("")){
            throw new IllegalArgumentException("url is empty");
        }
        mapControllers.put(url, controller);
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
                Controller controller = mapControllers.get(uri);
                controller.execute(request, response);
                if(!(controller instanceof JSONController)){
                    if(controller.getClass().isAnnotationPresent(RedirectToView.class)){
                        RedirectToView redirectToView=controller.getClass().getAnnotation(RedirectToView.class);
                        response.sendRedirect(redirectToView.value());
                    }else if(controller.getClass().isAnnotationPresent(ForwardToView.class)){
                        ForwardToView forwardToView=controller.getClass().getAnnotation(ForwardToView.class);
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
