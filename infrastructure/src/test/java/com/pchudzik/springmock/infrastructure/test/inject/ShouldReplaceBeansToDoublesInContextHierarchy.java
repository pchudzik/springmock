package com.pchudzik.springmock.infrastructure.test.inject;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import com.pchudzik.springmock.infrastructure.test.infrastructure.MatchingDoubleFactory;
import com.pchudzik.springmock.infrastructure.test.infrastructure.SpringMockContextBootstrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringRunner;

import static com.pchudzik.springmock.infrastructure.test.infrastructure.MatchingDoubleFactory.MatchingDoubleFactoryBuilder.byNameMockMatcher;
import static com.pchudzik.springmock.infrastructure.test.infrastructure.MatchingDoubleFactory.MatchingDoubleFactoryBuilder.byNameSpyMatcher;
import static org.junit.Assert.assertSame;

@ContextHierarchy({
		@ContextConfiguration(classes = ShouldReplaceBeansToDoublesInContextHierarchy.ChildConfig.class),
		@ContextConfiguration(classes = ShouldReplaceBeansToDoublesInContextHierarchy.ParentConfig.class)
})
@RunWith(SpringRunner.class)
@BootstrapWith(ShouldReplaceBeansToDoublesInContextHierarchy.TestContextBootstrapper.class)
public class ShouldReplaceBeansToDoublesInContextHierarchy {
	private static ChildService aChildMock = Mockito.mock(ChildService.class);
	private static ChildService aChildSpy = Mockito.mock(ChildService.class);
	private static ParentService aParentMock = Mockito.mock(ParentService.class);
	private static ParentService aParentSpy = Mockito.mock(ParentService.class);

	private static final String MOCK_CHILD = "childMock";
	private static final String MOCK_PARENT = "parentMock";
	private static final String SPY_CHILD = "childSpy";
	private static final String SPY_PARENT = "parentSpy";

	@AutowiredMock(name = MOCK_CHILD)
	@Qualifier(MOCK_CHILD)
	ChildService childMock;

	@AutowiredMock(name = MOCK_PARENT)
	@Qualifier(MOCK_PARENT)
	ParentService parentMock;

	@AutowiredSpy(name = SPY_CHILD)
	@Qualifier(SPY_CHILD)
	ChildService childSpy;

	@AutowiredSpy(name = SPY_PARENT)
	@Qualifier(SPY_PARENT)
	ParentService parentSpy;

	@Test
	public void should_replace_mocks_in_context_hierarchy() {
		assertSame(childMock, aChildMock);
		assertSame(parentMock, aParentMock);
	}

	@Test
	public void should_replace_spies_in_context_hierarchy() {
		assertSame(childSpy, aChildSpy);
		assertSame(parentSpy, aParentSpy);
	}

	@Configuration
	static class ChildConfig {
		@Bean(MOCK_CHILD)
		ChildService childServiceMocked() {
			return new ChildService();
		}

		@Bean(SPY_CHILD)
		ChildService childServiceSpied() {
			return new ChildService();
		}
	}

	@Configuration
	static class ParentConfig {
		@Bean(MOCK_PARENT)
		ParentService parentServiceMocked() {
			return new ParentService();
		}

		@Bean(SPY_PARENT)
		ParentService parentServiceSpied() {
			return new ParentService();
		}
	}

	static class TestContextBootstrapper extends SpringMockContextBootstrapper {
		public TestContextBootstrapper() {
			super(() -> MatchingDoubleFactory.builder()
					.mock(aChildMock, byNameMockMatcher(MOCK_CHILD))
					.mock(aParentMock, byNameMockMatcher(MOCK_PARENT))
					.spy(aChildSpy, byNameSpyMatcher(SPY_CHILD))
					.spy(aParentSpy, byNameSpyMatcher(SPY_PARENT))
					.build());
		}
	}

	static class ChildService {
	}

	static class ParentService {
	}
}
