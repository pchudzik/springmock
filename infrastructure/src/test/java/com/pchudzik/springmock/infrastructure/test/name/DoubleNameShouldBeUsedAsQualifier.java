package com.pchudzik.springmock.infrastructure.test.name;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import com.pchudzik.springmock.infrastructure.test.infrastructure.MatchingDoubleFactory;
import com.pchudzik.springmock.infrastructure.test.infrastructure.SpringMockContextBootstrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.junit4.SpringRunner;

import static com.pchudzik.springmock.infrastructure.test.infrastructure.MatchingDoubleFactory.MatchingDoubleFactoryBuilder.byNameMockMatcher;
import static com.pchudzik.springmock.infrastructure.test.infrastructure.MatchingDoubleFactory.MatchingDoubleFactoryBuilder.byNameSpyMatcher;
import static org.junit.Assert.assertNotEquals;

@RunWith(SpringRunner.class)
@BootstrapWith(DoubleNameShouldBeUsedAsQualifier.TestContextBootstrapper.class)
public class DoubleNameShouldBeUsedAsQualifier {
	private static final String MOCK1 = "mock1";
	private static final String MOCK2 = "mock2";
	private static final String SPY1 = "spy1";
	private static final String SPY2 = "spy2";

	@AutowiredMock(name = MOCK1)
	private MockService mockService1;

	@AutowiredMock(name = MOCK2)
	private MockService mockService2;

	@AutowiredSpy(name = SPY1)
	private SpyService spyService1;

	@AutowiredSpy(name = SPY2)
	private SpyService spyService2;

	@Test
	public void mock_name_should_be_used_as_qualifier() {
		assertNotEquals(mockService1, mockService2);
	}

	@Test
	public void spy_name_should_be_used_as_qualifier() {
		assertNotEquals(spyService1, spyService2);
	}

	private static class MockService {
	}

	private static class SpyService {
	}

	static class TestContextBootstrapper extends SpringMockContextBootstrapper {
		public TestContextBootstrapper() {
			super(() -> MatchingDoubleFactory.builder()
					.mock(new MockService(), byNameMockMatcher(MOCK1))
					.mock(new MockService(), byNameMockMatcher(MOCK2))
					.spy(new SpyService(), byNameSpyMatcher(SPY1))
					.spy(new SpyService(), byNameSpyMatcher(SPY2))
					.build());
		}
	}
}
