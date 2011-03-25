/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package carmesi.internal;

import java.io.IOException;
import org.junit.Before;
import javax.annotation.PreDestroy;
import javax.annotation.PostConstruct;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Victor
 */
public class SimpleObjectFactoryTest {

    private ObjectFactory factory;

    @Before
    public void setup() {
        factory = new SimpleObjectFactory();
    }

    @Test
    public void shouldCallPublicCallbacks() throws Exception {
        factory.init();
        ClassA a = factory.createController(ClassA.class);
        factory.dispose();
        assertTrue(a.initInvoked);
        assertTrue(a.destroyInvoked);
    }

    @Test
    public void shouldCallPrivateCallbacks() throws Exception {
        factory.init();
        ClassB b = factory.createController(ClassB.class);
        factory.dispose();
        assertTrue(b.initInvoked);
        assertTrue(b.destroyInvoked);
    }

    @Test
    public void shouldCallProtectedCallbacks() throws Exception {
        factory.init();
        ClassC c = factory.createController(ClassC.class);
        factory.dispose();
        assertTrue(c.initInvoked);
        assertTrue(c.destroyInvoked);
    }

    @Test
    public void shouldCallPackageCallbacks() throws Exception {
        factory.init();
        ClassD d = factory.createController(ClassD.class);
        factory.dispose();
        assertTrue(d.initInvoked);
        assertTrue(d.destroyInvoked);
    }
    
    @Test
    public void shouldCallChildAndParentCallbacks() throws Exception{
        factory.init();
        ChildClass child = factory.createController(ChildClass.class);
        factory.dispose();
        assertTrue(child.initInvoked);
        assertTrue(child.initChildInvoked);
        assertTrue(child.destroyInvoked);
        assertTrue(child.destroyChildInvoked);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumenException() throws Exception {
        factory.init();
        factory.createController(ClassE.class);
        fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumenExceptionToo() throws Exception {
        factory.init();
        factory.createController(ClassF.class);
        fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumenException3() throws Exception {
        factory.init();
        factory.createController(ClassG.class);
        fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumenException4() throws Exception {
        factory.init();
        factory.createController(ClassH.class);
        fail();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumenException5() throws Exception {
        factory.init();
        factory.createController(ClassI.class);
        fail();
    }
    
    public static class SuperClass{
        protected boolean initInvoked;
        protected boolean destroyInvoked;
        
    }

    public static class ClassA extends SuperClass{

        @PostConstruct
        public void init() {
            initInvoked = true;
        }

        @PreDestroy
        public void dispose() {
            destroyInvoked = true;
        }
    }

    public static class ClassB extends SuperClass{

        @PostConstruct
        private void init() {
            initInvoked = true;
        }

        @PreDestroy
        private void dispose() {
            destroyInvoked = true;
        }
    }

    public static class ClassC extends SuperClass{

        @PostConstruct
        protected void init() {
            initInvoked = true;
        }

        @PreDestroy
        protected void dispose() {
            destroyInvoked = true;
        }
        
    }

    public static class ClassD extends SuperClass{

        @PostConstruct
        void init() throws RuntimeException {
            initInvoked = true;
        }

        @PreDestroy
        void dispose() throws IllegalStateException {
            destroyInvoked = true;
        }
    }

    public static class ClassE extends SuperClass{

        @PostConstruct
        public void init() throws IllegalArgumentException, IOException {
            initInvoked = true;
        }
    }

    public static class ClassF extends SuperClass{

        @PreDestroy
        public String dispose() {
            return null;
        }
        
    }

    public static class ClassG extends SuperClass{

        @PreDestroy
        public String dispose(int a) {
            return null;
        }
        
    }

    public static class ClassH extends SuperClass{

        @PostConstruct
        static void init() {
        }
        
    }
    
    public static class ClassI extends SuperClass{

        @PostConstruct
        static void init() {
        }
        
        @PostConstruct
        static void init2() {
        }
        
    }
    
    public static class ChildClass extends ClassA{
        private boolean initChildInvoked;
        private boolean destroyChildInvoked;
        
        @PostConstruct
        public void initChild(){
            assertTrue(initInvoked);
            initChildInvoked=true;
        }
        
        @PreDestroy
        public void destroyChild(){
            assertFalse(destroyInvoked);
            destroyChildInvoked=true;
        }
        
    }
    
}