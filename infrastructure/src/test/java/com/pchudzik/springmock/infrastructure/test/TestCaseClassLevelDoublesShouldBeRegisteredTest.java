package com.pchudzik.springmock.infrastructure.test;

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

import static com.pchudzik.springmock.infrastructure.test.TestCaseClassLevelDoublesShouldBeRegisteredTest.MOCK_NAME;
import static com.pchudzik.springmock.infrastructure.test.TestCaseClassLevelDoublesShouldBeRegisteredTest.SPY_NAME;
import static org.junit.Assert.assertSame;

@RunWith(SpringRunner.class)
@BootstrapWith(TestCaseClassLevelDoublesShouldBeRegisteredTest.ContextBootstrapper.class)

@AutowiredMock(name = MOCK_NAME, doubleClass = TestCaseClassLevelDoublesShouldBeRegisteredTest.MyService.class)
@AutowiredSpy(name = SPY_NAME, doubleClass = TestCaseClassLevelDoublesShouldBeRegisteredTest.MyService.class)
public class TestCaseClassLevelDoublesShouldBeRegisteredTest {
	public static final String MOCK_NAME = "mock";
	public static final String SPY_NAME = "mock";

	@Autowired
	ApplicationContext applicationContext;

	@Test
	public void should_inject_mocks_into_application_context() {
		assertSame(
				new Object(),
				applicationContext.getBean(MOCK_NAME));
	}

	@Test
	public void should_inject_spies_into_application_context() {
		assertSame(
				new Object(),
				applicationContext.getBean(SPY_NAME));
	}

	static class MyService {
	}

	static class ContextBootstrapper extends SpringMockContextBootstrapper {
		ContextBootstrapper() {
			super(FixedDoubleFactory::instance);
		}
	}
}
