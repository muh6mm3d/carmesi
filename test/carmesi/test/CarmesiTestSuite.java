/* Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0) */

package carmesi.test;

import carmesi.internal.RegistratorListenerTest;
import carmesi.internal.TestForward;
import carmesi.internal.TestRedirect;
import carmesi.internal.simplecontrollers.TestCookieValue;
import carmesi.internal.simplecontrollers.TestParameterMappingFromAttributes;
import carmesi.internal.simplecontrollers.TestParameterMappingFromImplicitObjects;
import carmesi.internal.simplecontrollers.TestParameterMappingFromRequestParameters;
import carmesi.internal.simplecontrollers.TestParameterMappingFromRequestParametersCustom;
import carmesi.internal.simplecontrollers.TestProcessReturnValues;
import carmesi.internal.simplecontrollers.TestToJSON;
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
    GsonSerializerTest.class, JacksonSerializerTest.class, TestCookieValue.class})
public class CarmesiTestSuite {
}