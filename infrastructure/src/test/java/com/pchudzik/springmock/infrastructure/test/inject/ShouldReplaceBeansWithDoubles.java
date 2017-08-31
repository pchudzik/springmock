package com.pchudzik.springmock.infrastructure.test.inject;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import com.pchudzik.springmock.infrastructure.test.infrastructure.MatchingDoubleFactory;
import com.pchudzik.springmock.infrastructure.test.infrastructure.SpringMockContextBootstrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.junit4.SpringRunner;

import static com.pchudzik.springmock.infrastructure.test.infrastructure.MatchingDoubleFactory.MatchingDoubleFactoryBuilder.byNameMockMatcher;
import static com.pchudzik.springmock.infrastructure.test.infrastructure.MatchingDoubleFactory.MatchingDoubleFactoryBuilder.byNameSpyMatcher;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

@RunWith(SpringRunner.class)
@BootstrapWith(ShouldReplaceBeansWithDoubles.TestContextBootstrapper.class)
public class ShouldReplaceBeansWithDoubles {
	private static final Service aMock = Mockito.mock(Service.class);
	private static final Service aSpy = Mockito.mock(Service.class);

	private static final String MOCK = "mock";
	private static final String SPY = "spy";

	@AutowiredMock(name = MOCK)
	@Qualifier(MOCK)
	Service mock;

	@AutowiredSpy(name = SPY)
	@Qualifier(SPY)
	Service spy;

	@Autowired
	Config config;

	@Test
	public void should_replace_bean_with_mock() {
		assertSame(aMock, mock);
		assertNotSame(mock, config.notMock);
	}

	@Test
	public void should_replace_bean_with_spy() {
		assertSame(aSpy, spy);
		assertNotSame(spy, config.notSpy);
	}

	@Configuration
	static class Config {
		static final Service notMock = new Service();
		static final Service notSpy = new Service();

		@Bean(name = MOCK)
		Service notMock() {
			return notMock;
		}

		@Bean(name = SPY)
		Service notSpy() {
			return notSpy;
		}
	}

	static class TestContextBootstrapper extends SpringMockContextBootstrapper {
		public TestContextBootstrapper() {
			super(() -> MatchingDoubleFactory.builder()
					.mock(aMock, byNameMockMatcher(MOCK))
					.spy(aSpy, byNameSpyMatcher(SPY))
					.build());
		}
	}

	private static class Service {
	}
}
