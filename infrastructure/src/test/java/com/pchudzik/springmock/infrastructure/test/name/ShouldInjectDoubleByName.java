package com.pchudzik.springmock.infrastructure.test.name;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import com.pchudzik.springmock.infrastructure.test.infrastructure.MatchingDoubleFactory;
import com.pchudzik.springmock.infrastructure.test.infrastructure.SpringMockContextBootstrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.junit4.SpringRunner;

import static com.pchudzik.springmock.infrastructure.test.infrastructure.MatchingDoubleFactory.MatchingDoubleFactoryBuilder.byNameMockMatcher;
import static com.pchudzik.springmock.infrastructure.test.infrastructure.MatchingDoubleFactory.MatchingDoubleFactoryBuilder.byNameSpyMatcher;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@BootstrapWith(ShouldInjectDoubleByName.TestContextBootstrapper.class)
public class ShouldInjectDoubleByName {
	private static final Service aMock = new Service();
	private static final Service aSpy = new Service();

	private static final String MOCK = "mock";
	private static final String SPY = "spy";

	@AutowiredMock(name = MOCK)
	@Qualifier(MOCK)
	Service injectedMock;

	@AutowiredSpy(name = SPY)
	@Qualifier(SPY)
	Service injectedSpy;

	@Test
	public void should_inject_mock_by_name() {
		assertEquals(aMock, injectedMock);
	}

	@Test
	public void should_inject_spy_by_name() {
		assertEquals(aSpy, injectedSpy);
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
