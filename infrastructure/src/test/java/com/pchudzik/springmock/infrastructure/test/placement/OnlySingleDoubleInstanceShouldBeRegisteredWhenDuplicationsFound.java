package com.pchudzik.springmock.infrastructure.test.placement;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import com.pchudzik.springmock.infrastructure.test.infrastructure.MatchingDoubleFactory;
import com.pchudzik.springmock.infrastructure.test.infrastructure.SpringMockContextBootstrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;

import static com.pchudzik.springmock.infrastructure.test.infrastructure.MatchingDoubleFactory.MatchingDoubleFactoryBuilder.byNameMockMatcher;
import static com.pchudzik.springmock.infrastructure.test.infrastructure.MatchingDoubleFactory.MatchingDoubleFactoryBuilder.byNameSpyMatcher;
import static org.junit.Assert.assertSame;

@RunWith(SpringRunner.class)
@BootstrapWith(OnlySingleDoubleInstanceShouldBeRegisteredWhenDuplicationsFound.TestContextBootstrapper.class)

@AutowiredSpy(name = OnlySingleDoubleInstanceShouldBeRegisteredWhenDuplicationsFound.SPY, doubleClass = OnlySingleDoubleInstanceShouldBeRegisteredWhenDuplicationsFound.Service.class)
@AutowiredMock(name = OnlySingleDoubleInstanceShouldBeRegisteredWhenDuplicationsFound.MOCK, doubleClass = OnlySingleDoubleInstanceShouldBeRegisteredWhenDuplicationsFound.Service.class)
public class OnlySingleDoubleInstanceShouldBeRegisteredWhenDuplicationsFound {
	static final String SPY = "spy";
	static final String MOCK = "mock";

	@AutowiredSpy(name = SPY, doubleClass = Service.class)
	@Qualifier(SPY)
	Service spy1;

	@AutowiredSpy(name = SPY, doubleClass = Service.class)
	@Qualifier(SPY)
	Service spy2;

	@AutowiredMock(name = MOCK, doubleClass = Service.class)
	@Qualifier(MOCK)
	Service mock1;

	@AutowiredMock(name = MOCK, doubleClass = Service.class)
	@Qualifier(MOCK)
	Service mock2;

	@Autowired
	Config config;

	@Test
	public void should_register_single_mock_instance() {
		assertSame(mock1, mock2);
		assertSame(mock1, config.mock);
	}

	@Test
	public void should_register_single_spy_instance() {
		assertSame(spy1, spy2);
		assertSame(spy1, config.spy);
	}

	static class Service {
	}

	@Configuration
	@AutowiredSpy(name = SPY, doubleClass = Service.class)
	@AutowiredMock(name = MOCK, doubleClass = Service.class)
	static class Config {
		@AutowiredSpy(name = SPY, doubleClass = Service.class)
		@Qualifier(SPY)
		Service spy;

		@AutowiredMock(name = MOCK, doubleClass = Service.class)
		@Qualifier(MOCK)
		Service mock;
	}

	static class TestContextBootstrapper extends SpringMockContextBootstrapper {
		public TestContextBootstrapper() {
			super(() -> MatchingDoubleFactory.builder()
					.spy(Service::new, byNameSpyMatcher(SPY))
					.mock(Service::new, byNameMockMatcher(MOCK))
					.build());
		}
	}
}
