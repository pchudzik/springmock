package com.pchudzik.springmock.infrastructure.test.duplicates;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import com.pchudzik.springmock.infrastructure.test.infrastructure.MatchingDoubleFactory;
import com.pchudzik.springmock.infrastructure.test.infrastructure.SpringMockContextBootstrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.junit4.SpringRunner;

import static com.pchudzik.springmock.infrastructure.test.infrastructure.MatchingDoubleFactory.MatchingDoubleFactoryBuilder.byNameSpyMatcher;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@BootstrapWith(PartialMocksShouldBeDistinguishable.TestContextBootstrapper.class)
public class PartialMocksShouldBeDistinguishable {
	private static final SpyService aSpyService1 = new SpyService();
	private static final SpyService aSpyService2 = new SpyService();

	private static final String SPY1 = "spy1";
	private static final String SPY2 = "spy2";

	@AutowiredSpy
	private SpyService spy1;

	@AutowiredSpy
	private SpyService spy2;

	@Test
	public void should_distinguish_spies_of_the_same_class() {
		assertNotNull(spy1);
		assertEquals(aSpyService1, spy1);

		assertNotNull(spy2);
		assertEquals(aSpyService2, spy2);
	}

	static class TestContextBootstrapper extends SpringMockContextBootstrapper {
		protected TestContextBootstrapper() {
			super(() -> MatchingDoubleFactory.builder()
					.spy(aSpyService1, byNameSpyMatcher(SPY1))
					.spy(aSpyService2, byNameSpyMatcher(SPY2))
					.build());
		}
	}

	private static class SpyService {
	}
}
