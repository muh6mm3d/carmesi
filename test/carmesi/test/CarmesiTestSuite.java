/*
 */

package carmesi.test;

import carmesi.internal.RegistratorListenerTest;
import carmesi.internal.TestForward;
import carmesi.internal.TestRedirect;
import carmesi.internal.dynamic.TestParameterMappingFromAttributes;
import carmesi.internal.dynamic.TestParameterMappingFromImplicitObjects;
import carmesi.internal.dynamic.TestParameterMappingFromRequestParameters;
import carmesi.internal.dynamic.TestParameterMappingFromRequestParametersCustom;
import carmesi.internal.dynamic.TestProcessReturnValues;
import carmesi.internal.dynamic.TestToJSON;
import carmesi.jsonserializers.GsonSerializerTest;
import carmesi.jsonserializers.JacksonSerializerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author Victor
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({RegistratorListenerTest.class, TestForward.class, TestRedirect.class, 
    TestParameterMappingFromAttributes.class, TestParameterMappingFromImplicitObjects.class, TestParameterMappingFromRequestParameters.class,
    TestParameterMappingFromRequestParametersCustom.class, TestProcessReturnValues.class, TestToJSON.class,
    GsonSerializerTest.class, JacksonSerializerTest.class})
public class CarmesiTestSuite {
}