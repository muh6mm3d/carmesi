/* Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0) */

package carmesi.convert;

/**
 * Convert a string to a type T and viceversa. Used for parameter injection and for setting cookie values.
 * 
 * @author VictorHugo Herrera Maldonado
 * @param <T> The target value.
 */
public interface Converter<T> {

    /**
     * Converts the stringValue to the type T. Type and annotations for the target are provided in the TargetInfo parameter.
     * 
     * @param stringValue
     * @param info Contains the type and annotations of the target.
     * @throws ConverterException if the string can not be converted.
     * @return 
     */
    public T convertToObject(String stringValue, TargetInfo info) throws ConverterException;

    /**
     * Convert the object with type T to a String representation
     *  
     * @param value
     * @throws ConverterException if the object can not be converted to String.
     * @return String
     */
    public String convertToString(T value, TargetInfo info) throws ConverterException;
    
}
