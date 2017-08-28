package com.pchudzik.springmock.infrastructure.test.placement;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import com.pchudzik.springmock.infrastructure.test.infrastructure.FixedDoubleFactory;
import com.pchudzik.springmock.infrastructure.test.infrastructure.SpringMockContextBootstrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.junit4.SpringRunner;

import static com.pchudzik.springmock.infrastructure.test.placement.TestCaseLevelDoublesShouldBeRegistered.MOCK_NAME;
import static com.pchudzik.springmock.infrastructure.test.placement.TestCaseLevelDoublesShouldBeRegistered.SPY_NAME;
import static org.junit.Assert.assertSame;

@RunWith(SpringRunner.class)
@BootstrapWith(TestCaseLevelDoublesShouldBeRegistered.ContextBootstrapper.class)

@AutowiredMock(name = MOCK_NAME, doubleClass = TestCaseLevelDoublesShouldBeRegistered.MockService.class)
@AutowiredSpy(name = SPY_NAME, doubleClass = TestCaseLevelDoublesShouldBeRegistered.SpyService.class)
public class TestCaseLevelDoublesShouldBeRegistered {
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

	static class MockService {
	}

	static class SpyService {
	}

	static class ContextBootstrapper extends SpringMockContextBootstrapper {
		ContextBootstrapper() {
			super(() -> FixedDoubleFactory.builder()
					.mock(aMock)
					.spy(aSpy)
					.build());
		}
	}
}
