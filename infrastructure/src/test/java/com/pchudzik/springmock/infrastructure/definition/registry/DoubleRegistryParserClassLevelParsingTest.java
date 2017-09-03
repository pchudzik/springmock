package com.pchudzik.springmock.infrastructure.definition.registry;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import org.junit.Test;
import org.springframework.context.annotation.Configuration;

import static com.pchudzik.springmock.infrastructure.definition.DoubleDefinitionMatchers.doubleForClass;
import static com.pchudzik.springmock.infrastructure.definition.DoubleDefinitionMatchers.doubleWithName;
import static com.pchudzik.springmock.infrastructure.definition.registry.DoubleRegistryTestParser.parseClass;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class DoubleRegistryParserClassLevelParsingTest {
	@Test
	public void should_find_doubles_declared_on_test_class() {
		//when
		final DoubleRegistry doubleRegistry = parseClass(SimpleTestCase.class);

		//then
		assertThat(
				doubleRegistry.mockSearch(),
				contains(allOf(
						doubleForClass(MockService.class),
						doubleWithName(SimpleTestCase.MOCK_NAME))));
		assertThat(
				doubleRegistry.spySearch(),
				contains(allOf(
						doubleForClass(SpyService.class),
						doubleWithName(SimpleTestCase.SPY_NAME))));
	}

	@Test
	public void should_find_doubles_declared_on_configuration_class() {
		//when
		final DoubleRegistry doubleRegistry = parseClass(WithConfigurationTestCase.class);

		//then
		assertThat(
				doubleRegistry.mockSearch(),
				contains(allOf(
						doubleForClass(MockService.class),
						doubleWithName(WithConfigurationTestCase.MOCK_NAME))));
		assertThat(
				doubleRegistry.spySearch(),
				contains(allOf(
						doubleForClass(SpyService.class),
						doubleWithName(WithConfigurationTestCase.SPY_NAME))));
	}

	@Test
	public void autowired_mock_should_be_repeatable_annotation() {
		//when
		final DoubleRegistry doubleRegistry = parseClass(RepetableMocksTestCase.class);

		//then
		assertThat(
				doubleRegistry.mockSearch(),
				containsInAnyOrder(
						doubleWithName(RepetableMocksTestCase.MOCK_1_NAME),
						doubleWithName(RepetableMocksTestCase.MOCK_2_NAME)));
	}

	@Test
	public void autowired_spy_should_be_repeatable_annotation() {
		//when
		final DoubleRegistry doubleRegistry = parseClass(RepetableSpiesTestCase.class);

		//then
		assertThat(
				doubleRegistry.spySearch(),
				containsInAnyOrder(
						doubleWithName(RepetableSpiesTestCase.SPY_1_NAME),
						doubleWithName(RepetableSpiesTestCase.SPY_2_NAME)));
	}

	@Test
	public void mock_name_should_be_generated_based_on_double_class_for() {
		//when
		final DoubleRegistry doubleRegistry = parseClass(MockWithoutNameTestCase.class);

		//then
		assertThat(
				doubleRegistry.mockSearch(),
				contains(doubleWithName(MockWithoutNameTestCase.EXPECTED_MOCK_NAME)));
	}

	@Test
	public void mock_name_should_be_used_from_param_when_present() {
		//when
		final DoubleRegistry doubleRegistry = parseClass(MockWithCustomNameTestCase.class);

		//then
		assertThat(
				doubleRegistry.mockSearch(),
				contains(doubleWithName(MockWithCustomNameTestCase.MOCK_NAME)));
	}

	@Test
	public void spy_name_should_be_generated_based_on_double_class() {
		//when
		final DoubleRegistry doubleRegistry = parseClass(SpyWithoutNameTestCase.class);

		//then
		assertThat(
				doubleRegistry.spySearch(),
				contains(doubleWithName(SpyWithoutNameTestCase.EXPECTED_SPY_NAME)));
	}

	@Test
	public void spy_name_should_be_used_from_param_when_present() {
		//when
		final DoubleRegistry doubleRegistry = parseClass(SpyWithCustomNameTestCase.class);

		//then
		assertThat(
				doubleRegistry.spySearch(),
				contains(doubleWithName(SpyWithCustomNameTestCase.SPY_NAME)));
	}

	@AutowiredMock(doubleClass = MockService.class, name = SimpleTestCase.MOCK_NAME)
	@AutowiredSpy(doubleClass = SpyService.class, name = SimpleTestCase.SPY_NAME)
	static class SimpleTestCase {
		public static final String MOCK_NAME = "mock";
		public static final String SPY_NAME = "spy";
	}

	static class WithConfigurationTestCase {
		public static final String MOCK_NAME = "mock";
		public static final String SPY_NAME = "spy";

		@Configuration
		@AutowiredMock(doubleClass = MockService.class, name = WithConfigurationTestCase.MOCK_NAME)
		@AutowiredSpy(doubleClass = SpyService.class, name = WithConfigurationTestCase.SPY_NAME)
		static class Config {
		}
	}

	@AutowiredMock(name = RepetableMocksTestCase.MOCK_1_NAME, doubleClass = MockService.class)
	@AutowiredMock(name = RepetableMocksTestCase.MOCK_2_NAME, doubleClass = MockService.class)
	static class RepetableMocksTestCase {
		public static final String MOCK_1_NAME = "mock1";
		public static final String MOCK_2_NAME = "mock2";
	}

	@AutowiredSpy(name = RepetableSpiesTestCase.SPY_1_NAME, doubleClass = MockService.class)
	@AutowiredSpy(name = RepetableSpiesTestCase.SPY_2_NAME, doubleClass = MockService.class)
	static class RepetableSpiesTestCase {
		public static final String SPY_1_NAME = "spy1";
		public static final String SPY_2_NAME = "spy2";
	}

	@AutowiredMock(name = "mock")
	static class MockWithoutClassTestCase {
	}

	@AutowiredMock(doubleClass = MockService.class)
	static class MockWithoutNameTestCase {
		public static final String EXPECTED_MOCK_NAME = "mockService";
	}

	@AutowiredMock(doubleClass = MockService.class, name = MockWithCustomNameTestCase.MOCK_NAME)
	static class MockWithCustomNameTestCase {
		public static final String MOCK_NAME = "myMock";
	}

	@AutowiredSpy(name = "spy")
	static class SpyWithoutClassTestCase {
	}

	@AutowiredSpy(doubleClass = SpyService.class)
	static class SpyWithoutNameTestCase {
		public static final String EXPECTED_SPY_NAME = "spyService";
	}

	@AutowiredSpy(doubleClass = SpyService.class, name = SpyWithCustomNameTestCase.SPY_NAME)
	static class SpyWithCustomNameTestCase {
		public static final String SPY_NAME = "myMock";
	}

	interface MockService {
	}

	interface SpyService {
	}
}
