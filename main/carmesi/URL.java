/**
 * Insert license here.
 */

package carmesi;

/**
 * Indicates that a controller is going to serve the response when the specified url is requested.
 *
 * @author Victor Hugo Herrera Maldonado
 */
public @interface URL {
    /**
     * The string url to execute the controller.
     * 
     * @return
     */
    String value();
}
