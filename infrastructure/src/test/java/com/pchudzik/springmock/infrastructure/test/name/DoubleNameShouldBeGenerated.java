package com.pchudzik.springmock.infrastructure.test.name;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import com.pchudzik.springmock.infrastructure.test.infrastructure.MatchingDoubleFactory;
import com.pchudzik.springmock.infrastructure.test.infrastructure.SpringMockContextBootstrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.junit4.SpringRunner;

import static com.pchudzik.springmock.infrastructure.test.infrastructure.MatchingDoubleFactory.MatchingDoubleFactoryBuilder.byNameMockMatcher;
import static com.pchudzik.springmock.infrastructure.test.infrastructure.MatchingDoubleFactory.MatchingDoubleFactoryBuilder.byNameSpyMatcher;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@BootstrapWith(DoubleNameShouldBeGenerated.TestContextBootstrapper.class)
public class DoubleNameShouldBeGenerated {
	private static final MockService aMock = new MockService();
	private static final SpyService aSpy = new SpyService();

	private static final String MOCK = "mockService";
	private static final String SPY = "spyService";

	@AutowiredMock
	private MockService mockService;

	@AutowiredSpy
	private SpyService spyService;

	@Autowired
	ApplicationContext applicationContext;

	@Test
	public void should_use_generated_mock_names() {
		assertEquals(aMock, applicationContext.getBean(MOCK));
	}

	@Test
	public void should_use_generated_spy_names() {
		assertEquals(aSpy, applicationContext.getBean(SPY));
	}

	static class TestContextBootstrapper extends SpringMockContextBootstrapper {
		public TestContextBootstrapper() {
			super(() -> MatchingDoubleFactory.builder()
					.mock(aMock, byNameMockMatcher(MOCK))
					.spy(aSpy, byNameSpyMatcher(SPY))
					.build());
		}
	}

	private static class MockService {
	}

	private static class SpyService {
	}
}
