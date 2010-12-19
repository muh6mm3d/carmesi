/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package carmesi.umbrella;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Victor
 */
class DateConverter implements  Converter<Date>{

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
