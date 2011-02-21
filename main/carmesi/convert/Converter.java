package carmesi.convert;

/**
 * Convert a string to a target value. Used for parameter injection.
 * 
 * @author VictorHugo Herrera Maldonado
 * @param <T> The target value.
 */
public interface Converter<T> {

    /**
     * Converts the stringValue to the target type T. Type and annotations for the target are provided in the TargetInfo parameter.
     * 
     * @param stringValue
     * @param info Contains the type and annotations of the target.
     * @return 
     */
    public T convert(String stringValue, TargetInfo info);
    
}
