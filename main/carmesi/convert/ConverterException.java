/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package carmesi.convert;

/**
 * Throwed if a problem in the conversion occurs.
 * 
 * @author Victor
 */
public class ConverterException extends Exception {

    public ConverterException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConverterException(String message) {
        super(message);
    }
    
}
