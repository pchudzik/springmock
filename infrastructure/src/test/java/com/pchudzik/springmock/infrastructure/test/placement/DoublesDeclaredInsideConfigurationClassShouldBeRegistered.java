package com.pchudzik.springmock.infrastructure.test.placement;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import com.pchudzik.springmock.infrastructure.test.infrastructure.FixedDoubleFactory;
import com.pchudzik.springmock.infrastructure.test.infrastructure.SpringMockContextBootstrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@BootstrapWith(DoublesDeclaredInsideConfigurationClassShouldBeRegistered.TestContextBootstrapper.class)
public class DoublesDeclaredInsideConfigurationClassShouldBeRegistered {
	@Test
	public void should_inject_doubles_into_configuration_class() {
		//pass
	}

	@Configuration
	static class Config {
		@AutowiredMock
		Service mock;

		@AutowiredSpy
		Service spy;
	}

	static class TestContextBootstrapper extends SpringMockContextBootstrapper {
		public TestContextBootstrapper() {
			super(() -> FixedDoubleFactory.builder()
					.mock(new Service())
					.spy(new Service())
					.build());
		}
	}

	private static class Service {
	}
}
