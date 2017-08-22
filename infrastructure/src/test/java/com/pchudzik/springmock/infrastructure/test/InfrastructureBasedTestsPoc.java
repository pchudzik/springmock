package com.pchudzik.springmock.infrastructure.test;

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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;

@RunWith(SpringRunner.class)
@BootstrapWith(InfrastructureBasedTestsPoc.TestContextBootstrap.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class InfrastructureBasedTestsPoc {
	private static final String MOCK_NAME = "mock";
	private static final String SPY_NAME = "spy";

	@Autowired
	MockitoBasedDoubleFactory doubleFactory;

	@AutowiredMock
	MyMock mock;

	@AutowiredSpy
	MySpy spy;

	@Test
	public void should_inject_preconfigured_mock() {
		//given
		final ArgumentCaptor<DoubleDefinition> doubleDefinitionArgumentCaptor = ArgumentCaptor.forClass(DoubleDefinition.class);

		//expect
		Mockito
				.verify(doubleFactory.getMockedDoubleFactory())
				.createMock(doubleDefinitionArgumentCaptor.capture());
		assertEquals(doubleDefinitionArgumentCaptor.getValue().getName(), MOCK_NAME);
	}

	@Test
	public void should_inject_preconfigured_spy() {
		//given
		final ArgumentCaptor<DoubleDefinition> doubleDefinitionArgumentCaptor = ArgumentCaptor.forClass(DoubleDefinition.class);

		//expect
		Mockito
				.verify(doubleFactory.getMockedDoubleFactory())
				.createSpy(any(Object.class), doubleDefinitionArgumentCaptor.capture());
		assertEquals(doubleDefinitionArgumentCaptor.getValue().getName(), SPY_NAME);
	}

	static class TestContextBootstrap extends SpringMockContextBootstrapper {
		protected TestContextBootstrap() {
			super(() -> MockitoBasedDoubleFactory.builder()
					.mock(Mockito.mock(MyMock.class))
					.spy(Mockito.mock(MySpy.class))
					.build());
		}
	}

	static class MyMock {
	}

	static class MySpy {
	}
}
