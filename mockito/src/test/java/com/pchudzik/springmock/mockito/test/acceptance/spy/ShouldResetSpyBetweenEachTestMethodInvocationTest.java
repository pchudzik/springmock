package com.pchudzik.springmock.mockito.test.acceptance.spy;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.times;

/**
 * <p>Basic reset test for current basic implementation.
 * Proper tests should be introduced when implementing additional mock settings
 *
 * <p>For proper test see https://github.com/spring-projects/spring-boot/blob/master/spring-boot-test/src/test/java/org/springframework/boot/test/mock/mockito/ResetMocksTestExecutionListenerTests.java
 *
 * <p>The idea is simple configure test to run in particular order.
 * Then configure mock behaviour in the first test.
 * And expect default behaviour in the next test.
 */
@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ShouldResetSpyBetweenEachTestMethodInvocationTest {
	@AutowiredSpy
	AnyService anyService;

	@Test
	public void test_01() {
		//when
		anyService.hello();

		//then
		Mockito
				.verify(anyService, times(1))
				.hello();
	}

	@Test
	public void test_02() {
		//when
		anyService.hello();

		//then
		Mockito
				.verify(anyService, times(1))
				.hello();
	}

	@Configuration
	static class Config {
		@Bean
		AnyService anyService() {
			return new AnyService();
		}
	}

	private static class AnyService {
		String hello() {
			return "hello";
		}
	}
}
