package com.pchudzik.springmock.infrastructure.test.configuration;

import com.pchudzik.springmock.infrastructure.DoubleConfigurationParser;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import com.pchudzik.springmock.infrastructure.definition.DoubleDefinition;
import com.pchudzik.springmock.infrastructure.test.infrastructure.MockitoBasedDoubleFactory;
import com.pchudzik.springmock.infrastructure.test.infrastructure.SpringMockContextBootstrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

@RunWith(SpringRunner.class)
@BootstrapWith(ClassLevelDoublesConfigurationShouldBeResolved.TestContextBootstrapper.class)
public class ClassLevelDoublesConfigurationShouldBeResolved {
	private static final Object mockConfiguration = new Object();
	private static final Object spyConfiguration = new Object();
	private static final DoubleConfigurationParser configurationParser = Mockito.mock(DoubleConfigurationParser.class);
	private static final String SPY = "spy";
	private static final String MOCK = "mock";

	static {
		Mockito
				.when(configurationParser.parseMockConfiguration(eq(MOCK), any()))
				.thenReturn(mockConfiguration);
		Mockito
				.when(configurationParser.parseSpyConfiguration(eq(SPY), any()))
				.thenReturn(spyConfiguration);
	}

	@Autowired
	MockitoBasedDoubleFactory doubleFactory;

	@Test
	public void mock_configuration_should_be_resolved() {
		final ArgumentCaptor<DoubleDefinition> doubleDefinitionCaptor = ArgumentCaptor.forClass(DoubleDefinition.class);

		Mockito
				.verify(doubleFactory.getMockedDoubleFactory())
				.createMock(doubleDefinitionCaptor.capture());
		assertSame(
				mockConfiguration,
				doubleDefinitionCaptor.getValue().getConfiguration(Object.class));
	}

	@Test
	public void spy_configuration_should_be_resolved() {
		final ArgumentCaptor<DoubleDefinition> doubleDefinitionCaptor = ArgumentCaptor.forClass(DoubleDefinition.class);

		Mockito
				.verify(doubleFactory.getMockedDoubleFactory())
				.createSpy(any(), doubleDefinitionCaptor.capture());
		assertSame(
				spyConfiguration,
				doubleDefinitionCaptor.getValue().getConfiguration(Object.class));
	}

	@Configuration
	@AutowiredSpy(doubleClass = Service.class, name = SPY)
	@AutowiredMock(doubleClass = Service.class, name = MOCK)
	static class Config {
	}

	static class Service {
	}

	static class TestContextBootstrapper extends SpringMockContextBootstrapper {
		public TestContextBootstrapper() {
			super(
					() -> MockitoBasedDoubleFactory.builder()
							.spy(new DoublesConfigurationShouldBeResolved.Service())
							.mock(new DoublesConfigurationShouldBeResolved.Service())
							.build(),
					() -> configurationParser);
		}
	}
}
