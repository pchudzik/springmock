package com.pchudzik.springmock.infrastructure.test.inject;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import com.pchudzik.springmock.infrastructure.test.infrastructure.MatchingDoubleFactory;
import com.pchudzik.springmock.infrastructure.test.infrastructure.SpringMockContextBootstrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.junit4.SpringRunner;

import static com.pchudzik.springmock.infrastructure.test.infrastructure.MatchingDoubleFactory.MatchingDoubleFactoryBuilder.byNameMockMatcher;
import static com.pchudzik.springmock.infrastructure.test.infrastructure.MatchingDoubleFactory.MatchingDoubleFactoryBuilder.byNameSpyMatcher;
import static org.junit.Assert.assertSame;

@RunWith(SpringRunner.class)
@BootstrapWith(DoubleShouldBeInjectedWhenItsOnlyCandidate.TestContextBootstrapper.class)
public class DoubleShouldBeInjectedWhenItsOnlyCandidate {
	private static final MockDependency aMock = Mockito.mock(MockDependency.class);
	private static final SpyDependency aSpy = Mockito.mock(SpyDependency.class);
	private static final String MOCK = "mockedBeanName";
	private static final String SPY = "spiedBeanName";

	@Autowired
	Service service;

	@Test
	public void should_inject_mock_by_class_match() {
		assertSame(aMock, service.mockDependency);
	}

	@Test
	public void should_inject_spy_by_class_match() {
		assertSame(aSpy, service.spyDependency);
	}

	@Configuration
	static class Config {
		@AutowiredMock(name = MOCK)
		MockDependency mock;

		@AutowiredSpy(name = SPY)
		SpyDependency spy;

		@Bean
		Service service() {
			return new Service(mock, spy);
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

	static class SpyDependency {
	}
	static class MockDependency {
	}

	static class Service {
		private final MockDependency mockDependency;
		private final SpyDependency spyDependency;

		Service(MockDependency mockDependency, SpyDependency spyDependency) {
			this.mockDependency = mockDependency;
			this.spyDependency = spyDependency;
		}
	}
}
