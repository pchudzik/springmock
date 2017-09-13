package com.pchudzik.springmock.infrastructure.test.name;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import com.pchudzik.springmock.infrastructure.test.infrastructure.FixedDoubleFactory;
import com.pchudzik.springmock.infrastructure.test.infrastructure.SpringMockContextBootstrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

@RunWith(SpringRunner.class)
@ContextConfiguration
@BootstrapWith(DoubleNameShouldBeResolvedAutomaticallyToReplaceExistingBean.TestContextBootstrapper.class)
public class DoubleNameShouldBeResolvedAutomaticallyToReplaceExistingBean {
	private static final ServiceToMock aMock = Mockito.mock(ServiceToMock.class);
	private static final ServiceToSpy aSpy = Mockito.mock(ServiceToSpy.class);

	@AutowiredMock
	ServiceToMock mock;

	@AutowiredSpy
	ServiceToSpy spy;

	@Autowired
	ApplicationContext applicationContext;

	@Test
	public void should_replace_single_bean_with_mock() {
		assertSame(aMock, mock);
		assertEquals(
				1,
				applicationContext.getBeansOfType(ServiceToMock.class).size());
	}

	@Test
	public void should_replace_single_bean_with_spy() {
		assertSame(aSpy, spy);
		assertEquals(
				1,
				applicationContext.getBeansOfType(ServiceToSpy.class).size());
	}

	@Configuration
	static class Config {
		@Bean(name = "realServiceInstance1")
		ServiceToMock serviceToMock() {
			return new ServiceToMock();
		}

		@Bean(name = "realServiceInstance2")
		ServiceToSpy serviceToSpy() {
			return new ServiceToSpy();
		}
	}

	static class ServiceToMock {
	}

	static class ServiceToSpy {
	}

	static class TestContextBootstrapper extends SpringMockContextBootstrapper {
		public TestContextBootstrapper() {
			super(() -> FixedDoubleFactory.builder()
					.mock(aMock)
					.spy(aSpy)
					.build());
		}
	}
}
