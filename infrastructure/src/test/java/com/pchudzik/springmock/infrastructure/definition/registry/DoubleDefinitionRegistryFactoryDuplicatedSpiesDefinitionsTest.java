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
import static com.pchudzik.springmock.infrastructure.definition.registry.DoubleDefinitionTestFactory.parseClass;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class DoubleDefinitionRegistryFactoryDuplicatedSpiesDefinitionsTest {
	private static final String SPY_NAME = "spyService";

	@Parameters
	public static Collection<Object> parameters() {
		return asList(
				DuplicatedSpyOnTestCaseAndInTestCase_TestCase.class,
				DuplicatedSpyOnConfigurationClassAndInTestCase_TestCase.class,
				DuplicatedSpyOnTestCaseConfigurationAndInTestCase_TestCase.class,
				DuplicatedSpyOnTestCaseAndOnConfiguration_TestCase.class
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
						doubleForClass(SpyService.class),
						doubleWithName(SPY_NAME))));

	}

	@AutowiredMock(doubleClass = SpyService.class)
	static class DuplicatedSpyOnTestCaseAndInTestCase_TestCase {
		@AutowiredMock
		SpyService spyService;
	}

	static class DuplicatedSpyOnConfigurationClassAndInTestCase_TestCase {
		@AutowiredMock
		SpyService spyService;

		@Configuration
		@AutowiredMock(doubleClass = SpyService.class)
		static class Config {
		}
	}

	@AutowiredMock(doubleClass = SpyService.class)
	static class DuplicatedSpyOnTestCaseConfigurationAndInTestCase_TestCase {
		@AutowiredMock
		SpyService spyService;

		@Configuration
		@AutowiredMock(doubleClass = SpyService.class)
		static class Config {
		}
	}

	@AutowiredMock(doubleClass = SpyService.class)
	static class DuplicatedSpyOnTestCaseAndOnConfiguration_TestCase {
		@Configuration
		@AutowiredMock(doubleClass = SpyService.class)
		static class Config {
		}
	}

	interface SpyService {
	}
}
