package com.pchudzik.springmock.spock.configuration;

import org.spockframework.mock.IDefaultResponse;
import org.spockframework.mock.MockImplementation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface SpockDouble {

	/**
	 * <p>Configure arguments provider used for object creation</p>
	 *
	 * @see org.spockframework.mock.runtime.MockConfiguration#MockConfiguration(java.lang.String, java.lang.reflect.Type, java.lang.Object, org.spockframework.mock.MockNature, org.spockframework.mock.MockImplementation, java.util.Map)
	 */
	Class<? extends ConstructorArgumentsProvider> constructorArguments() default ConstructorArgumentsProvider.class;

	/**
	 * <p>Configure default response for mock</p>
	 *
	 * @see org.spockframework.mock.runtime.MockConfiguration#MockConfiguration(java.lang.String, java.lang.reflect.Type, java.lang.Object, org.spockframework.mock.MockNature, org.spockframework.mock.MockImplementation, java.util.Map)
	 */
	Class<? extends IDefaultResponse> defaultResponse() default IDefaultResponse.class;

	/**
	 * <p>Will create global mocks.</p>
	 *
	 * @see org.spockframework.mock.runtime.MockConfiguration#MockConfiguration(java.lang.String, java.lang.reflect.Type, java.lang.Object, org.spockframework.mock.MockNature, org.spockframework.mock.MockImplementation, java.util.Map)
	 */
	boolean global() default false;

	/**
	 * <p>Creates mocks using requested implementation</p>
	 *
	 * @see org.spockframework.mock.runtime.MockConfiguration#MockConfiguration(java.lang.String, java.lang.reflect.Type, java.lang.Object, org.spockframework.mock.MockNature, org.spockframework.mock.MockImplementation, java.util.Map)
	 */
	MockImplementation mockImplementation() default MockImplementation.JAVA;

	/**
	 * <p>Disables verification of mocks.</p>
	 *
	 * @see org.spockframework.mock.runtime.MockConfiguration#MockConfiguration(java.lang.String, java.lang.reflect.Type, java.lang.Object, org.spockframework.mock.MockNature, org.spockframework.mock.MockImplementation, java.util.Map)
	 */
	boolean stub() default false;

	/**
	 * <p>Allows to stub, mock and spy on objects with constructor which causes side effects</p>
	 *
	 * @see org.spockframework.mock.runtime.MockConfiguration#MockConfiguration(java.lang.String, java.lang.reflect.Type, java.lang.Object, org.spockframework.mock.MockNature, org.spockframework.mock.MockImplementation, java.util.Map)
	 */
	boolean useObjenesis() default false;

	interface ConstructorArgumentsProvider {
		List<Object> getConstructorArguments();
	}
}
