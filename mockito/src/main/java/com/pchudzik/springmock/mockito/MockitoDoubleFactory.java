package com.pchudzik.springmock.mockito;

import com.pchudzik.springmock.infrastructure.DoubleFactory;
import com.pchudzik.springmock.infrastructure.definition.DoubleDefinition;
import com.pchudzik.springmock.mockito.configuration.MockitoDoubleConfiguration;
import org.mockito.MockSettings;
import org.mockito.Mockito;
import org.springframework.aop.framework.AbstractAdvisingBeanPostProcessor;

import java.util.function.Supplier;

public class MockitoDoubleFactory implements DoubleFactory {
	private final Supplier<MockSettings> defaultSettingsProvider;

	public MockitoDoubleFactory() {
		this(Mockito::withSettings);
	}

	MockitoDoubleFactory(Supplier<MockSettings> defaultSettingsProvider) {
		this.defaultSettingsProvider = defaultSettingsProvider;
	}

	@Override
	public Object createMock(DoubleDefinition mockDefinition) {
		return Mockito
				.mock(
						mockDefinition.getDoubleClass(),
						prepareMockSettings(mockDefinition));
	}

	@Override
	public Object createSpy(Object bean, DoubleDefinition spyDefinition) {
		return Mockito.mock(
				spyDefinition.getDoubleClass(),
				prepareMockSettings(spyDefinition).spiedInstance(bean));
	}

	/**
	 * This will prevent spring from creating proxy for any annotations which causes to wrap bean in proxy (@Async for example)
	 *
	 * @see AbstractAdvisingBeanPostProcessor#postProcessAfterInitialization(java.lang.Object, java.lang.String)
	 * <p>
	 * I will probably need to revise this in the future because spock doesn't allow to create mocks from multiple interfaces :(
	 * https://github.com/spockframework/spock/issues/576
	 */
	private MockSettings prepareMockSettings(DoubleDefinition doubleDefinition) {
		return doubleDefinition
				.getConfiguration(MockitoDoubleConfiguration.class)
				.map(config -> config.createMockSettings(defaultSettingsProvider.get()))
				.orElseGet(defaultSettingsProvider::get);
	}
}
