package com.pchudzik.springmock.infrastructure.definition.registry;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;

import static com.pchudzik.springmock.infrastructure.definition.DoubleDefinitionMatchers.doubleForClass;
import static com.pchudzik.springmock.infrastructure.definition.DoubleDefinitionMatchers.doubleWithName;
import static com.pchudzik.springmock.infrastructure.definition.registry.DoubleRegistryTestParser.parseClass;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class DoubleRegistryParserDuplicatedMockDefinitionsTest {
	private static final String MOCK_NAME = "mockService";

	@Parameters
	public static Collection<Object> parameters() {
		return asList(
				DuplicatedMockOnTestCaseAndInTestCase_TestCase.class,
				DuplicatedMockOnConfigurationClassAndInTestCase_TestCase.class,
				DuplicatedMockOnTestCaseConfigurationAndInTestCase_TestCase.class,
				DuplicatedMockOnTestCaseAndOnConfiguration_TestCase.class
		);
	}

	@Parameter
	public Class<?> testClass;

	@Test
	public void only_one_instance_of_mock_should_be_created_when_mock_declared_on_class_and_injected_to_class() {
		//when
		final DoubleRegistry doubleRegistry = parseClass(testClass);

		//then
		assertThat(
				doubleRegistry.getMocks(),
				contains(allOf(
						doubleForClass(MockService.class),
						doubleWithName(MOCK_NAME))));

	}

	@AutowiredMock(doubleClass = MockService.class)
	static class DuplicatedMockOnTestCaseAndInTestCase_TestCase {
		@AutowiredMock
		MockService mockService;
	}

	static class DuplicatedMockOnConfigurationClassAndInTestCase_TestCase {
		@AutowiredMock
		MockService mockService;

		@Configuration
		@AutowiredMock(doubleClass = MockService.class)
		static class Config {
		}
	}

	@AutowiredMock(doubleClass = MockService.class)
	static class DuplicatedMockOnTestCaseConfigurationAndInTestCase_TestCase {
		@AutowiredMock
		MockService mockService;

		@Configuration
		@AutowiredMock(doubleClass = MockService.class)
		static class Config {
		}
	}

	@AutowiredMock(doubleClass = MockService.class)
	static class DuplicatedMockOnTestCaseAndOnConfiguration_TestCase {
		@Configuration
		@AutowiredMock(doubleClass = MockService.class)
		static class Config {
		}
	}

	interface MockService {
	}
}
