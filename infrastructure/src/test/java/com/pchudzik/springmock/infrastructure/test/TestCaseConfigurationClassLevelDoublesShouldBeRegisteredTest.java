package com.pchudzik.springmock.infrastructure.test;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import com.pchudzik.springmock.infrastructure.test.infrastructure.FixedDoubleFactory;
import com.pchudzik.springmock.infrastructure.test.infrastructure.SpringMockContextBootstrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertSame;

@RunWith(SpringRunner.class)
@BootstrapWith(TestCaseConfigurationClassLevelDoublesShouldBeRegisteredTest.ContextBootstrap.class)
public class TestCaseConfigurationClassLevelDoublesShouldBeRegisteredTest {
	public static final String MOCK_NAME = "mock";
	public static final String SPY_NAME = "spy";

	private static final MockService aMock = new MockService();
	private static final SpyService aSpy = new SpyService();

	@Autowired
	ApplicationContext applicationContext;

	@Test
	public void should_inject_mocks_into_application_context() {
		assertSame(
				aMock,
				applicationContext.getBean(MOCK_NAME));
	}

	@Test
	public void should_inject_spies_into_application_context() {
		assertSame(
				aSpy,
				applicationContext.getBean(SPY_NAME));
	}

	@Configuration
	@AutowiredMock(name = MOCK_NAME, doubleClass = MockService.class)
	@AutowiredSpy(name = SPY_NAME, doubleClass = SpyService.class)
	static class Config {
	}

	static class MockService {
	}

	static class SpyService {
	}

	static class ContextBootstrap extends SpringMockContextBootstrapper {
		protected ContextBootstrap() {
			super(() -> FixedDoubleFactory.builder()
					.mock(aMock)
					.spy(aSpy)
					.build());
		}
	}
}
