/* Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0) */

package carmesi.convert;

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

    public Date convertToObject(String stringValue, TargetInfo info) throws ConverterException {
        assert info != null;
        if(stringValue == null){
            return null;
        }
        try{
            DatePattern pattern = info.getAnnotation(DatePattern.class);
            if(pattern == null){
                throw new ConverterException("Date pattern not defined");
            }
            return new SimpleDateFormat(pattern.value()).parse(stringValue);
        }catch(ParseException ex){
            throw new ConverterException("Illegal date: "+stringValue);
        }
    }

    public String convertToString(Date value, TargetInfo info) throws ConverterException {
        if(value == null){
            return null;
        }
        DatePattern pattern = info.getAnnotation(DatePattern.class);
        if(pattern == null){
            throw new ConverterException("Date pattern not defined");
        }
        return new SimpleDateFormat(pattern.value()).format(value);
    }

}
