package com.pchudzik.springmock.infrastructure.test.duplicates;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.test.infrastructure.MatchingDoubleFactory;
import com.pchudzik.springmock.infrastructure.test.infrastructure.SpringMockContextBootstrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.junit4.SpringRunner;

import static com.pchudzik.springmock.infrastructure.test.infrastructure.MatchingDoubleFactory.MatchingDoubleFactoryBuilder.byNameMockMatcher;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@BootstrapWith(MultipleMocksShouldBeDistinguishable.TestContextBootstrapper.class)
public class MultipleMocksShouldBeDistinguishable {
	private static final MockService aMock1 = new MockService();
	private static final MockService aMock2 = new MockService();

	private final static String MOCK1 = "mock1";
	private final static String MOCK2 = "mock2";

	@AutowiredMock
	MockService mock1;

	@AutowiredMock
	MockService mock2;

	@Test
	public void created_mocks_should_be_distinguishable() {
		assertNotNull(mock1);
		assertEquals(aMock1, mock1);

		assertNotNull(mock2);
		assertEquals(aMock2, mock2);
	}

	private static class MockService {
	}

	static class TestContextBootstrapper extends SpringMockContextBootstrapper {
		public TestContextBootstrapper() {
			super(() -> MatchingDoubleFactory.builder()
					.mock(aMock1, byNameMockMatcher(MOCK1))
					.mock(aMock2, byNameMockMatcher(MOCK2))
					.build());
		}
	}
}
