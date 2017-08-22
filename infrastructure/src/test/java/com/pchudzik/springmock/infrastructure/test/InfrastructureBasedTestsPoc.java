package com.pchudzik.springmock.infrastructure.test;

import com.pchudzik.springmock.infrastructure.DoubleFactory;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import com.pchudzik.springmock.infrastructure.definition.DoubleDefinition;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;

@RunWith(SpringRunner.class)
@BootstrapWith(InfrastructureBasedTestsPoc.TestContextBootstrap.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class InfrastructureBasedTestsPoc {
	private static DoubleFactory mockDoubleFactory = Mockito.mock(DoubleFactory.class);
	private static final String MOCK_NAME = "mock";
	private static final String SPY_NAME = "spy";

	@BeforeClass
	public static void setupMockDoubleFactory() {
		final MyMock aMock = Mockito.mock(MyMock.class);
		final MySpy aSpy = Mockito.spy(new MySpy());

		Mockito
				.when(mockDoubleFactory.createMock(any(DoubleDefinition.class)))
				.thenReturn(aMock);

		Mockito
				.when(mockDoubleFactory.createSpy(any(Object.class), any(DoubleDefinition.class)))
				.thenReturn(aSpy);
	}

	@AutowiredMock
	MyMock mock;

	@AutowiredSpy
	MySpy spy;

	@After
	public void setup() {
		Mockito.reset(mockDoubleFactory);
	}

	@Test
	public void should_inject_preconfigured_mock() {
		//given
		final ArgumentCaptor<DoubleDefinition> doubleDefinitionArgumentCaptor = ArgumentCaptor.forClass(DoubleDefinition.class);

		//expect
		Mockito
				.verify(mockDoubleFactory)
				.createMock(doubleDefinitionArgumentCaptor.capture());
		assertEquals(doubleDefinitionArgumentCaptor.getValue().getName(), MOCK_NAME);
	}

	@Test
	public void should_inject_preconfigured_spy() {
		//given
		final ArgumentCaptor<DoubleDefinition> doubleDefinitionArgumentCaptor = ArgumentCaptor.forClass(DoubleDefinition.class);

		//expect
		Mockito
				.verify(mockDoubleFactory)
				.createSpy(any(Object.class), doubleDefinitionArgumentCaptor.capture());
		assertEquals(doubleDefinitionArgumentCaptor.getValue().getName(), SPY_NAME);
	}

	static class TestContextBootstrap extends SpringMockContextBootstrapper {
		protected TestContextBootstrap() {
			super(() -> mockDoubleFactory);
		}
	}

	static class MyMock {}
	static class MySpy {}
}
