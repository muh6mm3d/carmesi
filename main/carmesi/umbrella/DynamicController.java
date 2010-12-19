/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package carmesi.umbrella;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Victor
 */
public class DynamicController {
    private Object object;
    private Method method;
    private Map<Class, Converter> converters=new ConcurrentHashMap<Class, Converter>();
    
    private DynamicController(Object o, Method m){
        object=o;
        method=m;
        converters.put(Date.class, new DateConverter());
    }

    Method getMethod() {
        return method;
    }

    Object getObject() {
        return object;
    }
    
    public void destroy(){
        
    }
    
    public ControllerResult invoke(ExecutionContext context) throws IllegalAccessException, InvocationTargetException, InstantiationException, IntrospectionException {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Object[] actualParameters=new Object[parameterTypes.length];
        
        /* Iterates each parameter */
        for(int i=0; i < parameterTypes.length; i++){
            actualParameters[i]=getActualParameter(new TargetInfo(parameterTypes[i], parameterAnnotations[i]), context);
        }
        return new ControllerResult(method.invoke(object, actualParameters), method.getReturnType().equals(Void.class));
    }

    private Object getActualParameter(TargetInfo parameterInfo, ExecutionContext context) throws InstantiationException, IllegalAccessException, IntrospectionException, InvocationTargetException {
        /* Definir por tipos */
        if(parameterInfo.getParameterType().equals(ServletContext.class)){
            return context.getServletContext();
        }else if(ServletRequest.class.isAssignableFrom(parameterInfo.getParameterType())){
            return context.getRequest();
        }else if(ServletResponse.class.isAssignableFrom(parameterInfo.getParameterType())){
            return context.getResponse();
        }else if(HttpSession.class.isAssignableFrom(parameterInfo.getParameterType())){
            return context.getRequest().getSession();
        }else{
            /* Definir por anotaciones */
            RequestBean requestBean=parameterInfo.getAnnotation(RequestBean.class);
            if(requestBean != null){
                return fillBeanWithParameters(parameterInfo.getParameterType().newInstance(), context.getRequest());
            }
            RequestParameter requestParameter=parameterInfo.getAnnotation(RequestParameter.class);
            if(requestParameter != null){
                if(parameterInfo.getParameterType().isArray()){
                    String[] parameterValues = context.getRequest().getParameterValues(requestParameter.value());
                    Object array=null;
                    if(parameterValues != null){
                        array=asArray(parameterValues, parameterInfo);
                    }
                    return array;
                }else{
                    return convertStringToType(context.getRequest().getParameter(requestParameter.value()), parameterInfo);
                }
            }
            RequestAttribute requestAttribute=parameterInfo.getAnnotation(RequestAttribute.class);
            if(requestAttribute != null){
                return context.getRequest().getAttribute(requestAttribute.value());
            }
            SessionAttribute sessionAttribute=parameterInfo.getAnnotation(SessionAttribute.class);
            if(sessionAttribute != null){
                return context.getRequest().getSession().getAttribute(sessionAttribute.value());
            }
            ApplicationAttribute applicationAttribute=parameterInfo.getAnnotation(ApplicationAttribute.class);
            if(applicationAttribute != null){
                return context.getServletContext().getAttribute(applicationAttribute.value());
            }
            ContextParameter contextParameter=parameterInfo.getAnnotation(ContextParameter.class);
            if(contextParameter != null){
                return convertStringToType(context.getServletContext().getInitParameter(contextParameter.value()), parameterInfo);
            }
            CookieValue cookieValue=parameterInfo.getAnnotation(CookieValue.class);
            if(cookieValue != null){
                Cookie[] cookies = context.getRequest().getCookies();
                String string=null;
                if(cookies != null){
                    for(Cookie c:cookies){
                        if(c.getName().equals(cookieValue.value())){
                            string=c.getValue();
                        }
                    }
                }
                return convertStringToType(string, parameterInfo);
            }
            return null;
        }
    }
    
    private Object asArray(String[] stringValues, TargetInfo parameterInfo){
        assert stringValues != null;
        assert parameterInfo != null;
        Object array = Array.newInstance(parameterInfo.getParameterType().getComponentType(), stringValues.length);
        for(int i=0; i < stringValues.length; i++){
            Array.set(array, i, convertStringToType(stringValues[i], new TargetInfo(parameterInfo.getParameterType().getComponentType(), parameterInfo.getAnnotations())));
        }
        return array;
    }
    
    private Object convertStringToType(String string, TargetInfo parameterInfo){
        Class<?> targetType=parameterInfo.getParameterType();
        if(string == null){
            return null;
        }else if(targetType.equals(byte.class) || targetType.equals(Byte.class)){
            return Byte.valueOf(string);
        }else if(targetType.equals(short.class) || targetType.equals(Short.class)){
            return Short.valueOf(string);
        }else if(targetType.equals(int.class) || targetType.equals(Integer.class)){
            return Integer.valueOf(string);
        }else if(targetType.equals(long.class) || targetType.equals(Long.class)){
            return Long.valueOf(string);
        }else if(targetType.equals(char.class) || targetType.equals(Character.class)){
            if(string.length() != -1){
                throw new IllegalArgumentException("Invalid number of characters: "+string.length());
            }else{
                return Character.valueOf(string.charAt(0));
            }
        }else if(targetType.equals(BigDecimal.class)){
            return new BigDecimal(string);
        }else if(targetType.equals(boolean.class) || targetType.equals(Boolean.class)){
            return Boolean.valueOf(string);
        }else if(targetType.equals(String.class)){
            return string;
        }else{
            Converter<?> converter = converters.get(parameterInfo.getParameterType());
            if(converter != null){
                return converter.convert(string, parameterInfo);
            }
        }
        return null;
    }
    
    private <A extends Annotation> A getAnnotation(Annotation[]  annotations, Class<A> annotationClass){
        for(Annotation a: annotations){
            if(a.annotationType().equals(annotationClass)){
                return (A) a;
            }
        }
        return null;
    }
    
    private Object fillBeanWithParameters(Object object, HttpServletRequest request) throws IntrospectionException, IllegalAccessException, InvocationTargetException{
        BeanInfo beanInfo = Introspector.getBeanInfo(object.getClass());
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        if(propertyDescriptors != null){
            for(PropertyDescriptor descriptor: propertyDescriptors){
               String[] parameterValues = request.getParameterValues(descriptor.getName());
               if(parameterValues != null){
                   Object propertyValue;
                   if(descriptor.getPropertyType().isArray()){
                       propertyValue=asArray(parameterValues, new TargetInfo(descriptor.getPropertyType(), descriptor.getPropertyType().getAnnotations()));
                   }else{
                       propertyValue=convertStringToType(parameterValues[0], new TargetInfo(descriptor.getPropertyType(), descriptor.getPropertyType().getAnnotations()));
                   }
                   descriptor.getWriteMethod().invoke(object, propertyValue);
               }
            }
        }
        return object;
    }
    
    public static <T> DynamicController createDynamicController(Object object) {
        if(object.getClass().getDeclaredMethods().length != 1 && !Modifier.isPublic(object.getClass().getDeclaredMethods()[0].getModifiers())){
            throw new IllegalArgumentException("Controller must have one and only one public method.");
        }
        return new DynamicController(object, object.getClass().getDeclaredMethods()[0]);
    }
    
    public class ControllerResult{
        private Object value;
        private boolean isVoid;

        private ControllerResult(Object v, boolean b) {
            value=v;
            isVoid=b;
        }

        public boolean isVoid() {
            return isVoid;
        }

        public Object getValue() {
            return value;
        }
        
        public void writeToResponse(HttpServletResponse response) throws IOException{
            if(!isVoid){
                Gson gson=new GsonBuilder().create();
                response.getWriter().println(gson.toJson(value));
            }
        }

        public void process(ExecutionContext executionContext) {
            if(isVoid){
                return;
            }else{
                RequestAttribute requestAttribute=getAnnotation(method.getAnnotations(), RequestAttribute.class);
                if(requestAttribute != null){
                    executionContext.getRequest().setAttribute(requestAttribute.value(), value);
                    return;
                }
                SessionAttribute sessionAttribute=getAnnotation(method.getAnnotations(), SessionAttribute.class);
                if(sessionAttribute != null){
                    executionContext.getRequest().getSession().setAttribute(sessionAttribute.value(), value);
                    return;
                }
                ApplicationAttribute applicationAttribute=getAnnotation(method.getAnnotations(), ApplicationAttribute.class);
                if(applicationAttribute != null){
                    executionContext.getRequest().setAttribute(applicationAttribute.value(), value);
                    return;
                }
                if(value instanceof Cookie){
                    executionContext.getResponse().addCookie((Cookie) value);
                }
            }
        }
        
    }

}
