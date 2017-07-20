package com.pchudzik.springmock.mockito;

import com.pchudzik.springmock.infrastructure.definition.MockDefinition;
import com.pchudzik.springmock.infrastructure.definition.SpyDefinition;
import com.pchudzik.springmock.infrastructure.DoubleFactory;
import org.mockito.MockSettings;
import org.mockito.Mockito;
import org.springframework.aop.framework.AbstractAdvisingBeanPostProcessor;
import org.springframework.aop.framework.AopInfrastructureBean;

import static org.mockito.Mockito.withSettings;

public class MockitoDoubleFactory implements DoubleFactory {
	@Override
	public Object createMock(MockDefinition mockDefinition) {
		return Mockito
				.mock(
						mockDefinition.getDoubleClass(),
						prepareMockSettings());
	}

	@Override
	public Object createSpy(Object bean, SpyDefinition spyDefinition) {
		return Mockito.spy(bean);
	}

	/**
	 * This will prevent spring from creating proxy for any annotations which causes to wrap bean in proxy (@Async for example)
	 * @see AbstractAdvisingBeanPostProcessor#postProcessAfterInitialization(java.lang.Object, java.lang.String)
	 *
	 * I will probably need to revise this in the future because spock doesn't allow to create mocks from multiple interfaces :(
	 * https://github.com/spockframework/spock/issues/576
	 */
	private MockSettings prepareMockSettings() {
		return withSettings().extraInterfaces(AopInfrastructureBean.class);
	}
}
