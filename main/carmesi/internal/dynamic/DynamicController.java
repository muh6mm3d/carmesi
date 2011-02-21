package carmesi.internal.dynamic;

import carmesi.convert.TargetInfo;
import carmesi.convert.Converter;
import carmesi.convert.DateConverter;
import carmesi.HttpMethod;
import carmesi.RequestParameter;
import carmesi.RequestAttribute;
import carmesi.ContextParameter;
import carmesi.RequestBean;
import carmesi.ApplicationAttribute;
import carmesi.Controller;
import carmesi.SessionAttribute;
import carmesi.CookieValue;
import carmesi.ForwardTo;
import carmesi.RedirectTo;
import carmesi.ToJSON;
import carmesi.URL;
import carmesi.jsonserializers.JSONSerializer;
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
 * Wraps simple annotated POJOS in order to be used as a Controller. Its function is injecting parameters and processing the return value .
 * 
 * @author Victor Hugo Herrera Maldonado
 */
public class DynamicController implements Controller{
    private Object object;
    private Method method;
    private Map<Class, Converter> converters=new ConcurrentHashMap<Class, Converter>();
    private boolean autoRequestAttribute=true;
    private int defaultCookieMaxAge=-1;
    private JSONSerializer jsonSerializer;
    
    private DynamicController(Object o, Method m){
        object=o;
        method=m;
        addConverter(Date.class, new DateConverter());
    }
    
    public final <T> void addConverter(Class<T> klass, Converter<T> converter){
        converters.put(klass, converter);
    }
    
    public <T> Converter<T> getConverter(Class<T> klass){
        return converters.get(klass);
    }
    
    public Map<Class, Converter> getConverters(){
        return new HashMap<Class, Converter>(converters);
    }

    public boolean isAutoRequestAttribute() {
        return autoRequestAttribute;
    }

    public void setAutoRequestAttribute(boolean autoRequestAttribute) {
        this.autoRequestAttribute = autoRequestAttribute;
    }

    public int getDefaultCookieMaxAge() {
        return defaultCookieMaxAge;
    }

    public void setDefaultCookieMaxAge(int defaultCookieMaxAge) {
        this.defaultCookieMaxAge = defaultCookieMaxAge;
    }

    public JSONSerializer getJSONSerializer() {
        return jsonSerializer;
    }

    public void setJSONSerializer(JSONSerializer jsonSerializer) {
        this.jsonSerializer = jsonSerializer;
    }
    
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Result result=execute(new ExecutionContext(request, response));
        result.process();
    }
    
    public Result executeAndGetResult(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Result result=execute(new ExecutionContext(request, response));
        result.process();
        return result;
    }

    
    private Result execute(ExecutionContext context) throws IllegalAccessException, InvocationTargetException, InstantiationException, IntrospectionException {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Object[] actualParameters=new Object[parameterTypes.length];
        
        /* Iterates each parameter */
        for(int i=0; i < parameterTypes.length; i++){
            actualParameters[i]=getActualParameter(new TargetInfo(parameterTypes[i], parameterAnnotations[i]), context);
        }
        return new Result(method.invoke(object, actualParameters), method.getReturnType().equals(Void.TYPE), context);
    }

    private Object getActualParameter(TargetInfo parameterInfo, ExecutionContext context) throws InstantiationException, IllegalAccessException, IntrospectionException, InvocationTargetException {
        /* Definir por tipos */
        if(parameterInfo.getType().equals(ServletContext.class)){
            return context.getServletContext();
        }else if(ServletRequest.class.isAssignableFrom(parameterInfo.getType())){
            return context.getRequest();
        }else if(ServletResponse.class.isAssignableFrom(parameterInfo.getType())){
            return context.getResponse();
        }else if(HttpSession.class.isAssignableFrom(parameterInfo.getType())){
            return context.getRequest().getSession();
        }else{
            /* Definir por anotaciones */
            if(parameterInfo.isAnnotationPresent(RequestBean.class)){
                return fillBeanWithParameters(parameterInfo.getType().newInstance(), context.getRequest());
            }
            if(parameterInfo.isAnnotationPresent(RequestParameter.class)){
                RequestParameter requestParameter=parameterInfo.getAnnotation(RequestParameter.class);
                if(parameterInfo.getType().isArray()){
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
            if(parameterInfo.isAnnotationPresent(RequestAttribute.class)){
                return context.getRequest().getAttribute(parameterInfo.getAnnotation(RequestAttribute.class).value());
            }
            if(parameterInfo.isAnnotationPresent(SessionAttribute.class)){
                return context.getRequest().getSession().getAttribute(parameterInfo.getAnnotation(SessionAttribute.class).value());
            }
            if(parameterInfo.isAnnotationPresent(ApplicationAttribute.class)){
                return context.getServletContext().getAttribute(parameterInfo.getAnnotation(ApplicationAttribute.class).value());
            }
            if(parameterInfo.isAnnotationPresent(ContextParameter.class)){
                return convertStringToType(context.getServletContext().getInitParameter(parameterInfo.getAnnotation(ContextParameter.class).value()), parameterInfo);
            }
            if(parameterInfo.isAnnotationPresent(CookieValue.class)){
                Cookie[] cookies = context.getRequest().getCookies();
                String string=null;
                if(cookies != null){
                    for(Cookie c:cookies){
                        if(c.getName().equals(parameterInfo.getAnnotation(CookieValue.class).value())){
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
        Object array = Array.newInstance(parameterInfo.getType().getComponentType(), stringValues.length);
        for(int i=0; i < stringValues.length; i++){
            Array.set(array, i, convertStringToType(stringValues[i], new TargetInfo(parameterInfo.getType().getComponentType(), parameterInfo.getAnnotations())));
        }
        return array;
    }
    
    private Object convertStringToType(String string, TargetInfo parameterInfo){
        Class<?> targetType=parameterInfo.getType();
        Converter<?> converter = converters.get(parameterInfo.getType());
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

    public class Result{
        private Object value;
        private boolean isVoid;
        private ExecutionContext executionContext;

        private Result(Object v, boolean b, ExecutionContext context) {
            value=v;
            isVoid=b;
            executionContext=context;
        }

        /**
         * The value of the result.
         * 
         * @return
         */
        public Object getValue() {
            return value;
        }
        
        private Pattern getterPattern=Pattern.compile("get(.+)");

        private void process() throws IOException {
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
                    if(jsonSerializer != null){
                        executionContext.getResponse().getWriter().println(jsonSerializer.serialize(value));
                    }
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
