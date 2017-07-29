package com.pchudzik.springmock.mockito.configuration;

import org.mockito.listeners.InvocationListener;
import org.mockito.mock.SerializableMode;
import org.mockito.stubbing.Answer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface MockitoDouble {
	DoubleResetMode DEFAULT_RESET_MODE = DoubleResetMode.AFTER_TEST;

	/**
	 * <p>Allows to assign default answer to mock/spy instance</p>
	 *
	 * @see org.mockito.MockSettings#defaultAnswer(Answer)
	 */
	Class<? extends Answer> defaultAnswer() default Answer.class;

	/**
	 * @return default reset mode of mock/spy
	 */
	DoubleResetMode resetMode() default DoubleResetMode.AFTER_TEST;

	/**
	 * <p>Extra interfaces to be set for mock/spy</p>
	 *
	 * @see org.mockito.MockSettings#extraInterfaces(Class[])
	 */
	Class<?>[] extraInterfaces() default {};

	/**
	 * Allows to configure serializable mode of mock/spy
	 *
	 * @see org.mockito.MockSettings#serializable(SerializableMode)
	 */
	SerializableMode serializableMode() default SerializableMode.NONE;

	/**
	 * Will add extra verbosity to created mock
	 *
	 * @see org.mockito.MockSettings#verboseLogging()
	 */
	boolean verbose() default false;

	/**
	 * Will disable verification on mock/spy
	 *
	 * @see org.mockito.MockSettings#stubOnly()
	 */
	boolean stub() default false;

	/**
	 * <p>Registers one or more invocation listener.</p>
	 *
	 * @see org.mockito.MockSettings#invocationListeners(InvocationListener...)
	 */
	Class<? extends InvocationListener>[] invocationListeners() default {};

	enum DoubleResetMode {
		AFTER_TEST,
		BEFORE_TEST,
		NEVER
	}
}
