package com.pchudzik.springmock.spock;

import com.pchudzik.springmock.infrastructure.DoubleFactory;
import com.pchudzik.springmock.infrastructure.definition.DoubleDefinition;
import com.pchudzik.springmock.spock.configuration.SpockDoubleConfiguration;
import com.pchudzik.springmock.spock.configuration.SpockSettingsKeys;
import net.bytebuddy.ByteBuddy;
import org.springframework.aop.framework.AopInfrastructureBean;
import spock.mock.DetachedMockFactory;

import javax.annotation.Nullable;
import java.util.Map;

public class SpockDoubleFactory implements DoubleFactory {
	private final DetachedMockFactory mockFactory = new DetachedMockFactory();

	@Override
	public Object createMock(DoubleDefinition mockDefinition) {
		final Class<?> dynamicType = enhanceBeanClassWithAopInfrastructureBean(mockDefinition);
		return mockFactory.Mock(
				resolveConfiguration(mockDefinition),
				dynamicType);
	}

	@Override
	public Object createSpy(@Nullable Object bean, DoubleDefinition spyDefinition) {
		final Map<String, Object> spyConfiguration = resolveConfiguration(spyDefinition);
		spyConfiguration.put(SpockSettingsKeys.INSTANCE, bean);
		return mockFactory.Spy(spyConfiguration, spyDefinition.getDoubleClass());
	}

	private Map<String, Object> resolveConfiguration(DoubleDefinition doubleDefinition) {
		return doubleDefinition
				.getConfiguration(SpockDoubleConfiguration.class)
				.createDoubleConfiguration();
	}

	/**
	 * Spock doesn't support multiple interfaces mocking.
	 * <p>
	 * In order to get around it we create new class definition and add
	 * {@link AopInfrastructureBean} marker interface to it which will
	 * skip proxy creation for this class during bean post processing by
	 * {@link org.springframework.aop.framework.AbstractAdvisingBeanPostProcessor#postProcessAfterInitialization(Object, String)}
	 *
	 * @param mockDefinition
	 * @return original double class enhanced with {@link AopInfrastructureBean}
	 */
	private Class<?> enhanceBeanClassWithAopInfrastructureBean(DoubleDefinition mockDefinition) {
		return new ByteBuddy()
				.subclass(mockDefinition.getDoubleClass())
				.implement(AopInfrastructureBean.class)
				.make()
				.load(getClass().getClassLoader())
				.getLoaded();
	}
}
