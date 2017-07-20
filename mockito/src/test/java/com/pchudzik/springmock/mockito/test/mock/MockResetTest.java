package com.pchudzik.springmock.mockito.test.mock;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

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
public class MockResetTest {
	@AutowiredMock
	AnyService anyService;

	@Test
	public void test_01() {
		//given
		Mockito.when(anyService.hello()).thenReturn("mock");

		//expect
		assertEquals("mock", anyService.hello());
	}

	@Test
	public void test_02() {
		Assert.assertNull(anyService.hello());
	}

	private interface AnyService {
		String hello();
	}
}
