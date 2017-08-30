package com.pchudzik.springmock.infrastructure;

import com.pchudzik.springmock.infrastructure.definition.DoubleDefinition;
import com.pchudzik.springmock.infrastructure.spring.ToSpyReplacingProcessor;

import javax.annotation.Nullable;

/**
 * <p>Interface responsible for creating properly initialized and working mocks and spies instances.</p>
 */
public interface DoubleFactory {
	String DOUBLE_FACTORY_BEAN_NAME = MockConstants.PACKAGE_PREFIX + "doubleFactory";
	String CREATE_MOCK_FACTORY_METHOD = "createMock";
	String CREATE_SPY_FACTORY_METHOD = "createSpy";

	/**
	 * <p>Creates mock instance.</p>
	 * <p>
	 * <p>This method will be used as mock factory. Each parsed {@link DoubleDefinition}
	 * will be register as BeanDefinition in the context and factory method used to create mock instance will be this method.</p>
	 * <p>
	 * <p>Note that produced mock should be skipped from AOP processing by spring. As for right now
	 * it is mock factory responsibility to create mock with extra marker interface implemented -
	 * {@link org.springframework.aop.framework.AopInfrastructureBean}</p>
	 *
	 * @param mockDefinition mock configuration
	 * @return mock instance
	 */
	Object createMock(DoubleDefinition mockDefinition);

	/**
	 * <p>Creates spy instance</p>
	 * <p>
	 * <p>Spies are created by {@link ToSpyReplacingProcessor#postProcessAfterInitialization(Object, String)}</p>
	 *
	 * @param bean          object instance to spy on
	 * @param spyDefinition spy configuration
	 * @return spy of bean
	 */
	Object createSpy(@Nullable Object bean, DoubleDefinition spyDefinition);
}
