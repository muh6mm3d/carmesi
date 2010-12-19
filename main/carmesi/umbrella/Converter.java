package carmesi.umbrella;

interface Converter<T> {

    public T convert(String stringValue, TargetInfo info);
    
}
