package com.pchudzik.springmock.mockito.test.acceptance.mock;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@AutowiredMock(doubleClass = ClassLevelMocksShouldBeInjectableTest.TestCaseLevel.class)
public class ClassLevelMocksShouldBeInjectableTest {
	@Autowired
	ApplicationContext applicationContext;

	@Test
	public void should_inject_test_class_level_mocks() {
		final TestCaseLevel object = applicationContext.getBean(TestCaseLevel.class);

		assertTrue(Mockito.mockingDetails(object).isMock());
	}

	@Test
	public void should_inject_configuration_class_level_mocks() {
		final ConfigurationLevel object = applicationContext.getBean(ConfigurationLevel.class);

		assertTrue(Mockito.mockingDetails(object).isMock());
	}

	@Configuration
	@AutowiredMock(doubleClass = ConfigurationLevel.class)
	static class Config {

	}

	static class TestCaseLevel {
	}

	static class ConfigurationLevel {
	}
}
