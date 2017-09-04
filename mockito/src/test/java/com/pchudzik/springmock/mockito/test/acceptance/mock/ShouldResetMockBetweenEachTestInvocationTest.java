package com.pchudzik.springmock.mockito.test.acceptance.mock;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.times;

/**
 * <p>Basic reset test for current basic implementation.
 * Proper tests should be introduced when implementing additional mock settings</p>
 *
 * <p>For proper test see https://github.com/spring-projects/spring-boot/blob/master/spring-boot-test/src/test/java/org/springframework/boot/test/mock/mockito/ResetMocksTestExecutionListenerTests.java</p>
 *
 * <p>The idea is simple configure test to run in particular order.
 * Then configure mock behaviour in the first test.
 * And expect default behaviour in the next test.</p>
 */
@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ShouldResetMockBetweenEachTestInvocationTest {
	@AutowiredMock
	Service service;

	@Test
	public void test_01() {
		//when
		service.hello();

		//then
		Mockito
				.verify(service, times(1))
				.hello();
	}

	@Test
	public void test_02() {
		//when
		service.hello();

		//then
		Mockito
				.verify(service, times(1))
				.hello();
	}

	private interface Service {
		void hello();
	}
}
