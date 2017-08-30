package com.pchudzik.springmock.infrastructure.test.duplicates;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import com.pchudzik.springmock.infrastructure.test.infrastructure.MatchingDoubleFactory;
import com.pchudzik.springmock.infrastructure.test.infrastructure.SpringMockContextBootstrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.junit4.SpringRunner;

import static com.pchudzik.springmock.infrastructure.test.infrastructure.MatchingDoubleFactory.MatchingDoubleFactoryBuilder.byNameMockMatcher;
import static com.pchudzik.springmock.infrastructure.test.infrastructure.MatchingDoubleFactory.MatchingDoubleFactoryBuilder.byNameSpyMatcher;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@BootstrapWith(NamedDoublesShouldBeDistinguishable.TestContextBootstrapper.class)
public class NamedDoublesShouldBeDistinguishable {
	private static final MockService aMock1 = Mockito.mock(MockService.class);
	private static final MockService aMock2 = Mockito.mock(MockService.class);
	private static final SpyService aSpy1 = Mockito.mock(SpyService.class);
	private static final SpyService aSpy2 = Mockito.mock(SpyService.class);

	private static final String MOCK1 = "mock1";
	private static final String MOCK2 = "mock2";

	private static final String SPY1 = "spy1";
	private static final String SPY2 = "spy2";

	@AutowiredMock(name = MOCK1)
	MockService mock1;

	@AutowiredMock(name = MOCK2)
	MockService mock2;

	@AutowiredSpy(name = SPY1)
	SpyService spy1;

	@AutowiredSpy(name = SPY2)
	SpyService spy2;

	@Test
	public void named_mocks_should_be_distinguishable() {
		assertNotNull(mock1);
		assertEquals(aMock1, mock1);

		assertNotNull(mock1);
		assertEquals(aMock2, mock2);
	}

	@Test
	public void named_spies_should_be_distinguishable() {
		assertNotNull(spy1);
		assertEquals(aSpy1, spy1);

		assertNotNull(spy2);
		assertEquals(aSpy2, spy2);
	}

	private interface MockService {
	}

	private static class SpyService {
	}

	static class TestContextBootstrapper extends SpringMockContextBootstrapper {
		public TestContextBootstrapper() {
			super(() -> MatchingDoubleFactory.builder()
					.mock(aMock1, byNameMockMatcher(MOCK1))
					.mock(aMock2, byNameMockMatcher(MOCK2))
					.spy(aSpy1, byNameSpyMatcher(SPY1))
					.spy(aSpy2, byNameSpyMatcher(SPY2))
					.build());
		}
	}
}
