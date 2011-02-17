package carmesi.internal.dynamic;

public interface Converter<T> {

    public T convert(String stringValue, TargetInfo info);
    
}
