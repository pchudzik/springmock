package com.pchudzik.springmock.infrastructure.test.inject;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import com.pchudzik.springmock.infrastructure.test.infrastructure.FixedDoubleFactory;
import com.pchudzik.springmock.infrastructure.test.infrastructure.SpringMockContextBootstrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertSame;

@RunWith(SpringRunner.class)
@BootstrapWith(CreatedDoublesShouldBeInjectableIntoOtherBeans.TestContextBootstrapper.class)
public class CreatedDoublesShouldBeInjectableIntoOtherBeans {
	@AutowiredMock
	MockDependency mock;

	@AutowiredSpy
	SpyDependency spy;

	@Autowired
	Service service;

	@Autowired
	FixedDoubleFactory fixedDoubleFactory;

	@Test
	public void should_inject_mocks() {
		assertSame(fixedDoubleFactory.getMock(), service.mock);
	}

	@Test
	public void should_inject_spies() {
		assertSame(fixedDoubleFactory.getSpy(), service.spy);
	}

	@Configuration
	static class Config {
		@Bean
		Service service() {
			return new Service();
		}
	}

	static class Service {
		@Autowired
		MockDependency mock;

		@Autowired
		SpyDependency spy;
	}

	static class SpyDependency {
	}

	static class MockDependency {
	}

	static class TestContextBootstrapper extends SpringMockContextBootstrapper {
		public TestContextBootstrapper() {
			super(() -> FixedDoubleFactory.builder()
					.mock(new MockDependency())
					.spy(new SpyDependency())
					.build());
		}
	}
}
