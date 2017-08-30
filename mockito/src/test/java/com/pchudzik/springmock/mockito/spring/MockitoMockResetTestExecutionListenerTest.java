package com.pchudzik.springmock.mockito.spring;

import com.pchudzik.springmock.infrastructure.definition.DoubleDefinition;
import com.pchudzik.springmock.infrastructure.definition.registry.DoubleRegistry;
import com.pchudzik.springmock.infrastructure.spring.test.ApplicationContextCreator.*;
import com.pchudzik.springmock.mockito.configuration.MockitoDouble.DoubleResetMode;
import com.pchudzik.springmock.mockito.configuration.MockitoDoubleConfiguration;
import com.pchudzik.springmock.mockito.spring.MockitoMockResetTestExecutionListener.MockResetExecutor;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;

import java.util.stream.Stream;

import static com.pchudzik.springmock.infrastructure.spring.test.ApplicationContextCreator.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class MockitoMockResetTestExecutionListenerTest {
	private MockResetExecutor resetExecutor = Mockito.mock(MockResetExecutor.class);
	private MockitoMockResetTestExecutionListener executionListener = new MockitoMockResetTestExecutionListener(resetExecutor);

	@Test
	public void should_reset_only_mocks_and_spies() throws Exception {
		//given
		final TestContext testContext = createTestContext(
				new DoubleRegistry(emptyList(), emptyList()),
				Stream.of(
						bean("string1", "first string"),
						bean("string2", "second string")));

		//when
		executionListener.beforeTestMethod(testContext);
		executionListener.afterTestMethod(testContext);

		//then
		Mockito.verifyZeroInteractions(resetExecutor);
	}

	@Test
	public void should_reset_all_mocks_after_test_method() throws Exception {
		//given
		final Object mock1 = Mockito.mock(Object.class);
		final Object mock2 = Mockito.mock(Object.class);
		final TestContext testContext = createTestContext(
				new DoubleRegistry(
						asList(
								DoubleDefinition.builder()
										.name("mock1")
										.doubleClass(Object.class)
										.doubleConfiguration(resetAfterTestMethod())
										.build(),
								DoubleDefinition.builder()
										.name("mock2")
										.doubleClass(Object.class)
										.doubleConfiguration(resetAfterTestMethod())
										.build()),
						emptyList()),
				Stream.of(
						bean("string", "first string"),
						bean("mock1", mock1),
						bean("mock2", mock2)));

		//when
		executionListener.afterTestMethod(testContext);

		//then
		Mockito
				.verify(resetExecutor)
				.resetMock(mock1);
		Mockito
				.verify(resetExecutor)
				.resetMock(mock2);
		Mockito.verifyNoMoreInteractions(resetExecutor);
	}

	@Test
	public void should_reset_all_spies_after_test_method() throws Exception {
		//given
		final Object spy1 = Mockito.spy(new Object());
		final Object spy2 = Mockito.spy(new Object());
		final TestContext testContext = createTestContext(
				new DoubleRegistry(
						emptyList(),
						asList(
								DoubleDefinition.builder()
										.name("spy1")
										.doubleClass(Object.class)
										.doubleConfiguration(resetAfterTestMethod())
										.build(),
								DoubleDefinition.builder()
										.name("spy2")
										.doubleClass(Object.class)
										.doubleConfiguration(resetAfterTestMethod())
										.build())),
				Stream.of(
						bean("string", "first string"),
						bean("spy1", spy1),
						bean("spy2", spy2)));

		//when
		executionListener.afterTestMethod(testContext);

		//then
		Mockito
				.verify(resetExecutor)
				.resetMock(spy1);
		Mockito
				.verify(resetExecutor)
				.resetMock(spy2);
		Mockito.verifyNoMoreInteractions(resetExecutor);
	}

	@Test
	public void should_reset_mock_before_test_method() throws Exception {
		//given
		final Object mock = Mockito.mock(Object.class);
		final String mockName = "mock";
		final String noResetMockName = "no reset mock";
		final TestContext testContext = createTestContext(
				new DoubleRegistry(
						asList(
								DoubleDefinition.builder()
										.name(mockName)
										.doubleClass(Object.class)
										.doubleConfiguration(resetBeforeTestMethod())
										.build(),
								DoubleDefinition.builder()
										.name(noResetMockName)
										.doubleClass(Object.class)
										.doubleConfiguration(neverReset())
										.build()),
						emptyList()),
				Stream.of(
						bean(noResetMockName, Mockito.mock(Object.class)),
						bean(mockName, mock)));

		//when
		executionListener.beforeTestMethod(testContext);

		//then
		Mockito
				.verify(resetExecutor)
				.resetMock(mock);
		Mockito.verifyNoMoreInteractions(resetExecutor);
	}

	@Test
	public void should_never_reset_mock() throws Exception {
		//given
		final Object mock = Mockito.mock(Object.class, "mock to reset");
		final String noResetMockName = "no reset mock";
		final TestContext testContext = createTestContext(
				new DoubleRegistry(
						singletonList(
								DoubleDefinition.builder()
										.name(noResetMockName)
										.doubleClass(Object.class)
										.doubleConfiguration(neverReset())
										.build()),
						emptyList()),
				Stream.of(bean(noResetMockName, mock)));

		//when
		executionListener.beforeTestMethod(testContext);
		executionListener.afterTestMethod(testContext);

		//then
		Mockito.verifyZeroInteractions(resetExecutor);
	}

	@Test
	public void should_reset_mocks_from_parent_context() throws Exception {
		//given
		final Object parentMock = Mockito.mock(Object.class);
		final Object childSpy = Mockito.mock(Object.class);
		final DoubleRegistry doubleRegistry = new DoubleRegistry(
				singletonList(DoubleDefinition.builder()
						.name("parentMock")
						.doubleClass(Object.class)
						.doubleConfiguration(resetBeforeTestMethod())
						.build()),
				singletonList(DoubleDefinition.builder()
						.name("childSpy")
						.doubleClass(Object.class)
						.doubleConfiguration(resetAfterTestMethod())
						.build()));
		final ApplicationContext parentContext = buildAppContext(Stream.of(bean("parentMock", parentMock)));
		final ApplicationContext middleContext = buildAppContext(parentContext, Stream.of(bean("childSpy", childSpy)));
		final ApplicationContext childContext = buildAppContext(middleContext, Stream.of(withDoubleRegistry(doubleRegistry)));
		final TestContext testContext = mockTestContext(childContext);

		//when
		executionListener.beforeTestMethod(testContext);
		executionListener.afterTestMethod(testContext);

		//then
		Mockito
				.verify(resetExecutor)
				.resetMock(parentMock);
		Mockito
				.verify(resetExecutor)
				.resetMock(childSpy);
	}

	private MockitoDoubleConfiguration neverReset() {
		return MockitoDoubleConfiguration.builder()
				.resetMode(DoubleResetMode.NEVER)
				.build();
	}

	private MockitoDoubleConfiguration resetBeforeTestMethod() {
		return MockitoDoubleConfiguration.builder()
				.resetMode(DoubleResetMode.BEFORE_TEST)
				.build();
	}

	private MockitoDoubleConfiguration resetAfterTestMethod() {
		return MockitoDoubleConfiguration.builder()
				.resetMode(DoubleResetMode.AFTER_TEST)
				.build();
	}

	private TestContext createTestContext(DoubleRegistry doubleRegistry, Stream<TestBean> beanDefinitions) {
		return mockTestContext(buildAppContext(
				Stream.concat(
						Stream.of(withDoubleRegistry(doubleRegistry)),
						beanDefinitions)));
	}

	private TestContext mockTestContext(ApplicationContext applicationContext) {
		final TestContext testContext = Mockito.mock(TestContext.class);
		Mockito
				.when(testContext.getApplicationContext())
				.thenReturn(applicationContext);
		return testContext;
	}
}