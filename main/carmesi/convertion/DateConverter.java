package carmesi.convertion;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A conveter for java.util.Date. This converter requires that the target has an annotation DatePattern.
 * 
 * @author Victor Hugo Herrera Maldonado
 */
@ConverterFor(Date.class)
public class DateConverter implements  Converter<Date>{

    public Date convert(String stringValue, TargetInfo info) {
        try{
            DatePattern pattern = info.getAnnotation(DatePattern.class);
            if(pattern == null){
                throw new IllegalArgumentException("Date pattern not defined");
            }
            return new SimpleDateFormat(pattern.value()).parse(stringValue);
        }catch(ParseException ex){
            throw new IllegalArgumentException("Illegal date");
        }
    }

}
