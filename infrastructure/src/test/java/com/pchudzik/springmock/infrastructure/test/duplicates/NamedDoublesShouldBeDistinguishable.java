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
	private static final SpyService1 aSpy1 = Mockito.mock(SpyService1.class);
	private static final SpyService2 aSpy2 = Mockito.mock(SpyService2.class);

	private static final String MOCK1 = "mock1";
	private static final String MOCK2 = "mock2";

	private static final String SPY1 = "spy1";
	private static final String SPY2 = "spy2";

	@AutowiredMock(name = MOCK1)
	MockService mock1;

	@AutowiredMock(name = MOCK2)
	MockService mock2;

	@AutowiredSpy(name = SPY1)
	SpyService1 spyService1;

	@AutowiredSpy(name = SPY2)
	SpyService2 spyService2;

	@Test
	public void named_mocks_should_be_distinguishable() {
		assertNotNull(mock1);
		assertEquals(aMock1, mock1);

		assertNotNull(mock1);
		assertEquals(aMock2, mock2);
	}

	@Test
	public void named_spies_should_be_distinguishable() {
		assertNotNull(spyService1);
		assertEquals(aSpy1, spyService1);

		assertNotNull(spyService2);
		assertEquals(aSpy2, spyService2);
	}

	private interface MockService {
	}

	private static class SpyService1 {

	}

	private static class SpyService2 {
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
