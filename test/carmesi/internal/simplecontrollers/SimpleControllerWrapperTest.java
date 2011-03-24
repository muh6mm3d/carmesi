/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package carmesi.internal.simplecontrollers;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.xml.ws.WebServiceRef;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Victor
 */
public class SimpleControllerWrapperTest {

    @Test
    public void shouldCreateInstance() {
        SimpleControllerWrapper instance =
            SimpleControllerWrapper.createInstance(new Object() {

                public void execute() {
                }
            });
        assertNotNull(instance);
    }

    @Test
    public void shouldCreateInstanceWithCallbackMethods() {
        SimpleControllerWrapper instance =
            SimpleControllerWrapper.createInstance(new Object() {

                @PostConstruct
                public void init() {
                }

                @PreDestroy
                public void dispose() {
                }

                public void execute() {
                }
            });
        assertNotNull(instance);
    }

    @Test
    public void shouldCreateInstanceWithResourceInjection() {
        SimpleControllerWrapper instance =
            SimpleControllerWrapper.createInstance(new Object() {

                @Resource
                public void setA(Object a) {
                }

                public void execute() {
                }
            });
        assertNotNull(instance);
    }

    @Test
    public void shouldCreateInstanceWithCDIInjection() {
        SimpleControllerWrapper instance =
            SimpleControllerWrapper.createInstance(new Object() {

                @Inject
                public void setA(Object a) {
                }

                public void execute() {
                }
            });
        assertNotNull(instance);
    }

    @Test
    public void shouldCreateInstanceWithEJBInjection() {
        SimpleControllerWrapper instance =
            SimpleControllerWrapper.createInstance(new Object() {

                @EJB
                public void setA(Object a) {
                }

                public void execute() {
                }
            });
        assertNotNull(instance);
    }
    
    @Test
    public void shouldCreateInstanceWithWebServiceRefInjection() {
        SimpleControllerWrapper instance =
            SimpleControllerWrapper.createInstance(new Object() {

                @WebServiceRef
                public void setA(Object a) {
                }

                public void execute() {
                }
            });
        assertNotNull(instance);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void shouldNotCreateInstance() {
        SimpleControllerWrapper instance =
            SimpleControllerWrapper.createInstance(new Object() {
                
                @PostConstruct
                public void init(){
                    
                }
                
                @PreDestroy
                public void dispose(){
                    
                }
                
                @Resource
                public void setResource(Object o){
                    
                }
                
                @Inject
                public void setBean(Object o){
                    
                }
                
                @EJB
                public void setEJB(Object a) {
                    
                }
                
                @WebServiceRef
                public void setWebServiceRef(Object a){
                    
                }

                
            });
        fail();
    }
    
    
    @Test(expected=IllegalArgumentException.class)
    public void shouldNotCreateInstanceToo() {
        SimpleControllerWrapper instance =
            SimpleControllerWrapper.createInstance(new Object() {
                
                public void execute(){
                    
                }
                
                public void execute2(){
                    
                }
                
            });
        fail();
    }
    
}