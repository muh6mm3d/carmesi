/* Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0) */

package carmesi.convert;

/**
 * Throwed if a problem in the conversion occurs.
 * 
 * @author Victor Hugo Herrera Maldonado
 */
public class ConverterException extends Exception {

    public ConverterException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConverterException(String message) {
        super(message);
    }
    
}
