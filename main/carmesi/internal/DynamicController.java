/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package carmesi.internal;

import carmesi.HttpMethod;
import carmesi.RequestParameter;
import carmesi.RequestAttribute;
import carmesi.ContextParameter;
import carmesi.RequestBean;
import carmesi.ApplicationAttribute;
import carmesi.SessionAttribute;
import carmesi.CookieValue;
import carmesi.ForwardTo;
import carmesi.RedirectTo;
import carmesi.ToJSON;
import carmesi.URL;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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
class DynamicController implements  ControllerWrapper{
    private Object object;
    private Method method;
    private Map<Class, Converter> converters=new ConcurrentHashMap<Class, Converter>();
    private boolean autoRequestAttribute=true;
    private int defaultCookieMaxAge=-1;
    
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
    
    public void addConverter(Class<?> klass, Converter converter){
        converters.put(klass, converter);
    }

    public Result execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return execute(new ExecutionContext(request, response));
    }

    public String getForwardTo() {
        ForwardTo forwardTo = object.getClass().getAnnotation(ForwardTo.class);
        return forwardTo != null? forwardTo.value() : null;
    }

    public String getRedirectTo() {
        RedirectTo redirectTo = object.getClass().getAnnotation(RedirectTo.class);
        return redirectTo != null? redirectTo.value() : null;
    }

    public HttpMethod[] getHttpMethods() {
        URL url=object.getClass().getAnnotation(URL.class);
        return url != null ? HttpMethod.values() : (url.httpMethods().length == 0 ? HttpMethod.values(): url.httpMethods());
    }
    
    public ControllerResult execute(ExecutionContext context) throws IllegalAccessException, InvocationTargetException, InstantiationException, IntrospectionException {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Object[] actualParameters=new Object[parameterTypes.length];
        
        /* Iterates each parameter */
        for(int i=0; i < parameterTypes.length; i++){
            actualParameters[i]=getActualParameter(new TargetInfo(parameterTypes[i], parameterAnnotations[i]), context);
        }
        return new ControllerResult(method.invoke(object, actualParameters), method.getReturnType().equals(Void.TYPE), context);
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
        Converter<?> converter = converters.get(parameterInfo.getParameterType());
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
        }else if(converter != null){
            return converter.convert(string, parameterInfo);
        }else{
            Method[] methods = targetType.getDeclaredMethods();
            for(Method m: methods){
                if(m.getName().equals("valueOf") && m.getParameterTypes().length == 1 && m.getParameterTypes()[0].equals(String.class)
                        && m.getReturnType().equals(targetType) && Modifier.isStatic(m.getModifiers()) && Modifier.isPublic(m.getModifiers())){
                    try {
                        return m.invoke(null, string);
                    } catch (IllegalAccessException ex) {
                        throw new AssertionError(ex);
                    } catch (IllegalArgumentException ex) {
                        throw new AssertionError(ex);
                    } catch (InvocationTargetException ex) {
                        throw new AssertionError(ex);
                    }
                }
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
        List<Method> methods=new LinkedList<Method>();
        for(Method method:object.getClass().getDeclaredMethods()){
            if(Modifier.isPublic(method.getModifiers()) && !method.isAnnotationPresent(PostConstruct.class) && !method.isAnnotationPresent(PreDestroy.class)){
                methods.add(method);
            }
        }
        if(methods.size() != 1){
            throw new IllegalArgumentException("Controller must have one and only one public method.");
        }
        return new DynamicController(object, methods.get(0));
    }

    public class ControllerResult implements  ControllerWrapper.Result{
        private Object value;
        private boolean isVoid;
        private ExecutionContext executionContext;

        private ControllerResult(Object v, boolean b, ExecutionContext context) {
            value=v;
            isVoid=b;
            executionContext=context;
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
        
        private Pattern getterPattern=Pattern.compile("get(.+)");

        public void process() {
            if(isVoid){
                return;
            }else{
                if(method.isAnnotationPresent(RequestAttribute.class)){
                    executionContext.getRequest().setAttribute(method.getAnnotation(RequestAttribute.class).value(), value);
                }else if(method.isAnnotationPresent(SessionAttribute.class)){
                    executionContext.getRequest().getSession().setAttribute(method.getAnnotation(SessionAttribute.class).value(), value);
                }else if(method.isAnnotationPresent(ApplicationAttribute.class)){
                    executionContext.getServletContext().setAttribute(method.getAnnotation(ApplicationAttribute.class).value(), value);
                }else if(method.isAnnotationPresent(CookieValue.class)){
                    Cookie cookie=new Cookie(method.getAnnotation(CookieValue.class).value(), String.valueOf(value));
                    cookie.setMaxAge(defaultCookieMaxAge);
                    executionContext.getResponse().addCookie(cookie);
                }else if(value instanceof Cookie){
                    executionContext.getResponse().addCookie((Cookie) value);
                }else if(method.isAnnotationPresent(ToJSON.class)){
                    
                }else{
                    if(autoRequestAttribute){
                        Matcher matcher = getterPattern.matcher(method.getName());
                        if(matcher.matches()){
                            StringBuilder builder=new StringBuilder(matcher.group(1));
                            builder.setCharAt(0, Character.toLowerCase(builder.charAt(0)));
                            executionContext.getRequest().setAttribute(builder.toString(), value);
                        }
                    }
                }
            }
        }
        
    }

}
