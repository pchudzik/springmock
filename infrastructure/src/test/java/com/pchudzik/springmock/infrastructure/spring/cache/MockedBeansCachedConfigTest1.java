package com.pchudzik.springmock.infrastructure.spring.cache;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;

@BootstrapWith(CachedContextBootstrapper.class)
@RunWith(SpringRunner.class)
public class MockedBeansCachedConfigTest1 {
	@AutowiredMock
	Object anyService;

	@Test
	public void context_should_be_cached() {
		//given
		final int cachedContextsSize = CachedContextBootstrapper.getCachedContextsSize("MockedBeansCachedConfigTest");

		//expect
		assertTrue(
				"Expected cached contexts " + cachedContextsSize + " to be less or equal 1",
				cachedContextsSize <= 1);
	}
}
