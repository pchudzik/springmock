package com.pchudzik.springmock.spock;

import com.pchudzik.springmock.infrastructure.DoubleFactory;
import com.pchudzik.springmock.infrastructure.definition.MockDefinition;
import com.pchudzik.springmock.infrastructure.definition.SpyDefinition;
import net.bytebuddy.ByteBuddy;
import org.springframework.aop.framework.AopInfrastructureBean;
import spock.mock.DetachedMockFactory;

public class SpockDoubleFactory implements DoubleFactory {
	private final DetachedMockFactory mockFactory = new DetachedMockFactory();

	@Override
	public Object createMock(MockDefinition mockDefinition) {
		final Class<?> dynamicType = enhanceBeanClassWithAopInfrastructureBean(mockDefinition);
		return mockFactory.Mock(dynamicType);
	}

	@Override
	public Object createSpy(Object bean, SpyDefinition spyDefinition) {
		return mockFactory.Spy(bean);
	}

	/**
	 * Spock doesn't support multiple interfaces mocking.
	 *
	 * In order to get around it we create new class definition and add
	 * {@link AopInfrastructureBean} marker interface to it which will
	 * skip proxy creation for this class during bean post processing by
	 * {@link org.springframework.aop.framework.AbstractAdvisingBeanPostProcessor#postProcessAfterInitialization(Object, String)}
	 *
	 * @param mockDefinition
	 *
	 * @return original double class enhanced with {@link AopInfrastructureBean}
	 */
	private Class<?> enhanceBeanClassWithAopInfrastructureBean(MockDefinition mockDefinition) {
		return new ByteBuddy()
				.subclass(mockDefinition.getDoubleClass())
				.implement(AopInfrastructureBean.class)
				.make()
				.load(getClass().getClassLoader())
				.getLoaded();
	}
}
