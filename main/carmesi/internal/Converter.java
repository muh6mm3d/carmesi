package carmesi.internal;

interface Converter<T> {

    public T convert(String stringValue, TargetInfo info);
    
}
