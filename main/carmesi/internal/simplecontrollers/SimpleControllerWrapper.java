/* Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0) */

package carmesi.internal.simplecontrollers;

import carmesi.convert.TargetInfo;
import carmesi.convert.Converter;
import carmesi.convert.DateConverter;
import carmesi.RequestParameter;
import carmesi.RequestAttribute;
import carmesi.ContextParameter;
import carmesi.RequestBean;
import carmesi.ApplicationAttribute;
import carmesi.Controller;
import carmesi.SessionAttribute;
import carmesi.CookieValue;
import carmesi.ToJSON;
import carmesi.convert.ConverterException;
import carmesi.json.JSONSerializer;
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
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Wraps simple annotated POJOS in order to be used as a Controller. Its function is injecting parameters and processing the return value .
 * 
 * @author Victor Hugo Herrera Maldonado
 */
public class SimpleControllerWrapper implements Controller{
    private Object simpleController;
    private Method method;
    private Map<Class, Converter> converters=new ConcurrentHashMap<Class, Converter>();
    private boolean autoRequestAttribute=true;
    private int defaultCookieMaxAge=-1;
    private JSONSerializer jsonSerializer;
    
    private static ResourceBundle messagesBundle;
    
    static {
        messagesBundle=ResourceBundle.getBundle("carmesi.internal.simplecontrollers.injectionErrorMessages");
    }
    
    private SimpleControllerWrapper(Object simpleController, Method m){
        assert simpleController != null;
        assert m != null;
        this.simpleController=simpleController;
        method=m;
        addConverter(Date.class, new DateConverter());
    }
    
    /**
     * 
     * @param <T>
     * @param klass
     * @param converter
     * @throws NullPointerException if class or converter is null
     */
    public final <T> void addConverter(Class<T> klass, Converter<T> converter) throws NullPointerException {
        if(klass == null){
            throw new NullPointerException("class is null");
        }
        if(converter == null){
            throw new NullPointerException("converter is null");
        }
        converters.put(klass, converter);
    }
    
    /**
     * 
     * @param <T>
     * @param klass
     * @throws NullPointerException if klass is null
     * @return 
     */
    public <T> Converter<T> getConverter(Class<T> klass) throws NullPointerException {
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

    /**
     * 
     * @param defaultCookieMaxAge 
     * @see Cookie.setMaxAge
     */
    public void setDefaultCookieMaxAge(int defaultCookieMaxAge) {
        this.defaultCookieMaxAge = defaultCookieMaxAge;
    }

    public JSONSerializer getJSONSerializer() {
        return jsonSerializer;
    }

    /**
     * 
     * @param jsonSerializer
     * @throws NullPointerException if jsonSerializer is null
     */
    public void setJSONSerializer(JSONSerializer jsonSerializer) throws  NullPointerException{
        if (jsonSerializer == null) {
            throw new NullPointerException("jsonSerializer is null");
        }
        this.jsonSerializer = jsonSerializer;
    }
    
    /**
     * Executes the controller.
     * 
     * @param request
     * @param response
     * @throws NullPointerException if request or response is null
     * @throws Exception is an exception occurs when invoking the pojo controller.
     */
    public void execute(HttpServletRequest request, HttpServletResponse response) throws NullPointerException, Exception {
        executeAndGetResult(request, response);
    }
    
    /**
     * Executes the controller.
     * 
     * @param request
     * @param response
     * @throws NullPointerException if request or response is null
     * @throws Exception is an exception occurs when invoking the pojo controller
     * @return Result the result of the execution.
     */
    public Result executeAndGetResult(HttpServletRequest request, HttpServletResponse response) throws NullPointerException, Exception {
        if(request == null){
            throw new NullPointerException("request is null");
        }
        if(response == null){
            throw new NullPointerException("response is null");
        }
        Result result=execute(new ExecutionContext(request, response));
        result.process();
        return result;
    }

    private void checkValidAsignation(TargetInfo parameterInfo, Object attributeValue, String attributeName, String attributeType) throws IllegalArgumentException {
        Class type = parameterInfo.getType();
        if(type.isPrimitive()){
            if(attributeValue == null){
                String message=formatMessage(messagesBundle.getString("nullPrimitiveAttribute"), attributeType, attributeName, parameterInfo.getType().getName());
                throw new IllegalArgumentException(message);
            }
            if(type.equals(int.class)){
                type=Integer.class;
            }else if(type.equals(byte.class)){
                type=Byte.class;
            }else if(type.equals(short.class)){
                type=Short.class;
            }else if(type.equals(long.class)){
                type=Long.class;
            }else if(type.equals(float.class)){
                type=Float.class;
            }else if(type.equals(double.class)){
                type=Double.class;
            }else if(type.equals(boolean.class)){
                type=Boolean.class;
            }else if(type.equals(char.class)){
                type=Character.class;
            }
        }
        if(attributeValue != null && ! type.isAssignableFrom(attributeValue.getClass())){
            String message=formatMessage(messagesBundle.getString("attribute"), attributeType, attributeName, attributeValue.getClass().getName(), parameterInfo.getType().getName());
            throw new IllegalArgumentException(message);
        }
    }

    
    private Result execute(ExecutionContext context) throws IllegalAccessException, InvocationTargetException, InstantiationException, IntrospectionException {
        assert context != null;
        
        /* Parameter injection */
        Class<?>[] parameterTypes = method.getParameterTypes();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Object[] actualParameters=new Object[parameterTypes.length];
        for(int i=0; i < parameterTypes.length; i++){
            actualParameters[i]=getActualParameter(new TargetInfo(parameterTypes[i], parameterAnnotations[i]), context);
        }
        
        return new Result(method.invoke(simpleController, actualParameters), method.getReturnType().equals(Void.TYPE), context);
    }
    
    private Object getActualParameter(TargetInfo parameterInfo, ExecutionContext context) throws InstantiationException, IllegalAccessException, IntrospectionException, InvocationTargetException {
        assert parameterInfo != null;
        assert context != null;
        /* Injection by type */
        if(parameterInfo.getType().equals(ServletContext.class)){
            return context.getServletContext();
        }else if(HttpServletRequest.class.isAssignableFrom(parameterInfo.getType())){
            return context.getRequest();
        }else if(HttpServletResponse.class.isAssignableFrom(parameterInfo.getType())){
            return context.getResponse();
        }else if(HttpSession.class.isAssignableFrom(parameterInfo.getType())){
            return context.getRequest().getSession();
        }else{
            /* Injection by annotations */
            if(parameterInfo.isAnnotationPresent(RequestBean.class)){
                return fillBeanWithParameters(parameterInfo.getType().newInstance(), context.getRequest());
            }
            if(parameterInfo.isAnnotationPresent(RequestParameter.class)){
                String requestParameterName=parameterInfo.getAnnotation(RequestParameter.class).value();
                if(parameterInfo.getType().isArray()){
                    String[] parameterValues = context.getRequest().getParameterValues(requestParameterName);
                    Object array=null;
                    if(parameterValues != null){
                        try{
                            array=asArray(parameterValues, parameterInfo);
                        }catch(IllegalArgumentException ex){
                            String message=formatMessage(messagesBundle.getString("parameter"), "request", requestParameterName, Arrays.toString(parameterValues), parameterInfo.getType());
                            throw new IllegalArgumentException(message, ex);
                        }
                    }
                    return array;
                }else{
                    String requestParameterValue = context.getRequest().getParameter(requestParameterName);
                    try{
                        return convertStringToType(requestParameterValue, parameterInfo);
                    }catch(IllegalArgumentException ex){
                        String message=formatMessage(messagesBundle.getString("parameter"), "request", requestParameterName, requestParameterValue, parameterInfo.getType());
                        throw new IllegalArgumentException(message, ex);
                    }catch(ConverterException ex){
                        String message=formatMessage(messagesBundle.getString("parameter"), "request", requestParameterName, requestParameterValue, parameterInfo.getType());
                        throw new IllegalArgumentException(message, ex);
                    }
                }
            }
            if(parameterInfo.isAnnotationPresent(RequestAttribute.class)){
                String attributeName = parameterInfo.getAnnotation(RequestAttribute.class).value();
                Object attributeValue = context.getRequest().getAttribute(attributeName);
                checkValidAsignation(parameterInfo, attributeValue, attributeName, "request");
                return attributeValue;
            }
            if(parameterInfo.isAnnotationPresent(SessionAttribute.class)){
                String attributeName = parameterInfo.getAnnotation(SessionAttribute.class).value();
                Object attributeValue = context.getRequest().getSession().getAttribute(attributeName);
                checkValidAsignation(parameterInfo, attributeValue, attributeName, "session");
                return attributeValue;
            }
            if(parameterInfo.isAnnotationPresent(ApplicationAttribute.class)){
                String attributeName = parameterInfo.getAnnotation(ApplicationAttribute.class).value();
                Object attributeValue = context.getServletContext().getAttribute(attributeName);
                checkValidAsignation(parameterInfo, attributeValue, attributeName, "application");
                return attributeValue;
            }
            if(parameterInfo.isAnnotationPresent(ContextParameter.class)){
                ContextParameter contextParameter = parameterInfo.getAnnotation(ContextParameter.class);
                String contextParameterValue = context.getServletContext().getInitParameter(contextParameter.value());
                try{
                    return convertStringToType(contextParameterValue, parameterInfo);
                }catch(IllegalArgumentException ex){
                    String message=formatMessage(messagesBundle.getString("parameter"), "context", contextParameter.value(), contextParameterValue, parameterInfo.getType());
                    throw new IllegalArgumentException(message, ex);
                }catch(ConverterException ex){
                    String message=formatMessage(messagesBundle.getString("parameter"), "context", contextParameter.value(), contextParameterValue, parameterInfo.getType());
                    throw new IllegalArgumentException(message, ex);
                }
            }
            if(parameterInfo.isAnnotationPresent(CookieValue.class)){
                String cookieName=parameterInfo.getAnnotation(CookieValue.class).value();
                Cookie[] cookies = context.getRequest().getCookies();
                String cookieValue=null;
                if(cookies != null){
                    for(Cookie c:cookies){
                        if(c.getName().equals(cookieName)){
                            cookieValue=c.getValue();
                        }
                    }
                }
                try{
                    return convertStringToType(cookieValue, parameterInfo);
                }catch(IllegalArgumentException ex){
                    String message=formatMessage(messagesBundle.getString("cookie"), cookieName, cookieValue, parameterInfo.getType());
                    throw new IllegalArgumentException(message, ex);
                }catch(ConverterException ex){
                    String message=formatMessage(messagesBundle.getString("cookie"), cookieName, cookieValue, parameterInfo.getType());
                    throw new IllegalArgumentException(message, ex);
                }
            }
            return null;
        }
    }
    
    private Object asArray(String[] stringValues, TargetInfo parameterInfo){
        assert stringValues != null;
        assert parameterInfo != null;
        Object array = Array.newInstance(parameterInfo.getType().getComponentType(), stringValues.length);
        for(int i=0; i < stringValues.length; i++){
            try{
                Array.set(array, i, convertStringToType(stringValues[i], new TargetInfo(parameterInfo.getType().getComponentType(), parameterInfo.getAnnotations())));
            }catch(IllegalArgumentException ex){
                String message=formatMessage(messagesBundle.getString("array"), stringValues[i], parameterInfo.getType());
                throw new IllegalArgumentException(message, ex);
            }catch(ConverterException ex){
                String message=formatMessage(messagesBundle.getString("array"), stringValues[i], parameterInfo.getType());
                throw new IllegalArgumentException(message, ex);
            }
        }
        return array;
    }
    
    private Object convertStringToType(String string, TargetInfo parameterInfo) throws ConverterException{
        assert parameterInfo != null;
        Class<?> targetType=parameterInfo.getType();
        Converter<?> converter = converters.get(parameterInfo.getType());
        if(string == null){
            if(parameterInfo.getType().isPrimitive()){
                String message=formatMessage(messagesBundle.getString("nullPrimitiveConversion"), parameterInfo.getType());
                throw new IllegalArgumentException(message);
            }
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
        }else if(targetType.equals(BigInteger.class)){
            return new BigInteger(string);
        }else if(targetType.equals(boolean.class) || targetType.equals(Boolean.class)){
            return Boolean.valueOf(string);
        }else if(targetType.equals(String.class)){
            return string;
        }else if(converter != null){
            return converter.convertToObject(string, parameterInfo);
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
        assert object != null;
        assert request != null;
        BeanInfo beanInfo = Introspector.getBeanInfo(object.getClass());
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        if(propertyDescriptors != null){
            for(PropertyDescriptor descriptor: propertyDescriptors){
               String[] parameterValues = request.getParameterValues(descriptor.getName());
               if(parameterValues != null){
                   Object propertyValue;
                   try{
                       if(descriptor.getPropertyType().isArray()){
                           propertyValue=asArray(parameterValues, new TargetInfo(descriptor.getPropertyType(), descriptor.getPropertyType().getAnnotations()));
                       }else{
                            propertyValue=convertStringToType(parameterValues[0], new TargetInfo(descriptor.getPropertyType(), descriptor.getPropertyType().getAnnotations()));
                       }
                   }catch(IllegalArgumentException ex){
                       String value=descriptor.getPropertyType().isArray() ? Arrays.toString(parameterValues) : parameterValues[0];
                       String message=formatMessage(messagesBundle.getString("property"), descriptor.getName(), value, object.getClass(), descriptor.getName(), descriptor.getPropertyType());
                       throw new IllegalArgumentException(message, ex);
                   }catch(ConverterException ex){
                       String value=descriptor.getPropertyType().isArray() ? Arrays.toString(parameterValues) : parameterValues[0];
                       String message=formatMessage(messagesBundle.getString("property"), descriptor.getName(), value, object.getClass(), descriptor.getName(), descriptor.getPropertyType());
                       throw new IllegalArgumentException(message, ex);
                   }
                   descriptor.getWriteMethod().invoke(object, propertyValue);
               }
            }
        }
        return object;
    }
    
    /**
     * Create an instance of SimpleControllerWrapper with the specified POJO controller.
     * 
     * @param <T>
     * @param pojoController A POJO for using it as a Controller.
     * @return
     * @throws NullPointerException if object is null.
     */
    public static <T> SimpleControllerWrapper createInstance(Object pojoController) throws  NullPointerException{
        if(pojoController == null){
            throw new NullPointerException("controller object is null");
        }
        List<Method> methods=new LinkedList<Method>();
        for(Method method:pojoController.getClass().getDeclaredMethods()){
            if(Modifier.isPublic(method.getModifiers()) 
                    && !method.isAnnotationPresent(PostConstruct.class) && !method.isAnnotationPresent(PreDestroy.class)
                    && !method.isAnnotationPresent(Resource.class) && !isAnnotationPresent(method, "javax.ejb.EJB") && !isAnnotationPresent(method, "javax.inject.Inject")
                    && !isAnnotationPresent(method, "javax.xml.ws.WebServiceRef") ){
                methods.add(method);
            }
        }
        if(methods.size() != 1){
            throw new IllegalArgumentException("Controller must have one and only one public method.");
        }
        return new SimpleControllerWrapper(pojoController, methods.get(0));
    }
    
    /**
     * Uses reflexion to test for an annotation with the given class name. 
     * 
     * It's used because the annotation class could not exists in the executing environment (For example: annotation javax.ejb.EJB in Tomcat).
     * 
     * @return 
     */
    
    private static boolean isAnnotationPresent(Method method, String annotationClassname){
        try {
            Class<? extends Annotation> annotation = Class.forName(annotationClassname).asSubclass(Annotation.class);
            return method.isAnnotationPresent(annotation);
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

    private String formatMessage(String messagePattern, Object... values) {
        MessageFormat formatter=new MessageFormat(messagePattern);
        return formatter.format(values);
    }

    public class Result{
        private Object value;
        private boolean isVoid;
        private ExecutionContext executionContext;

        private Result(Object v, boolean b, ExecutionContext context) {
            assert context != null;
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
                    Converter<Object> converter=value != null? getConverter((Class<Object>)value.getClass()): null;
                    String stringValue;
                    if(converter != null){
                        try{
                            TargetInfo targetInfo = new TargetInfo(method.getReturnType(), method.getAnnotations());
                            stringValue=converter.convertToString(value, targetInfo);
                        }catch(ConverterException ex){
                            throw new RuntimeException(ex);
                        }
                    }else{
                        stringValue=String.valueOf(value);
                    }
                    Cookie cookie=new Cookie(method.getAnnotation(CookieValue.class).value(), stringValue);
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
