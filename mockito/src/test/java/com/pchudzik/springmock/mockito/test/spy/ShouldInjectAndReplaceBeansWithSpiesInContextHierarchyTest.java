package com.pchudzik.springmock.mockito.test.spy;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringRunner;

import static com.pchudzik.springmock.mockito.test.spy.ShouldInjectAndReplaceBeansWithSpiesInContextHierarchyTest.ChildService.CHILD_RESPONSE;
import static com.pchudzik.springmock.mockito.test.spy.ShouldInjectAndReplaceBeansWithSpiesInContextHierarchyTest.ParentService.PARENT_PREFIX_RESPONSE;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@ContextHierarchy({
		@ContextConfiguration(classes = ShouldInjectAndReplaceBeansWithSpiesInContextHierarchyTest.ChildConfig.class),
		@ContextConfiguration(classes = ShouldInjectAndReplaceBeansWithSpiesInContextHierarchyTest.ParentConfig.class)
})
public class ShouldInjectAndReplaceBeansWithSpiesInContextHierarchyTest {
	@AutowiredSpy
	ChildService childService;

	@Autowired
	ParentService parentService;

	@Test
	public void should_replace_beans_with_spies_in_hierarchical_configuration() {
		//when
		final String result = parentService.hello();

		//then
		assertEquals(
				PARENT_PREFIX_RESPONSE + CHILD_RESPONSE,
				result);
		Mockito
				.verify(childService)
				.hello();
	}

	@Configuration
	static class ChildConfig {
		@Bean
		ChildService childService() {
			return new ChildService();
		}
	}

	@Configuration
	static class ParentConfig {
		@Bean
		ParentService parentService() {
			return new ParentService();
		}
	}

	static class ChildService {
		static final String CHILD_RESPONSE = "child";

		String hello() {
			return CHILD_RESPONSE;
		}
	}

	static class ParentService {
		static final String PARENT_PREFIX_RESPONSE = "parent says hello ";

		@Autowired
		ChildService childService;

		String hello() {
			return PARENT_PREFIX_RESPONSE + childService.hello();
		}
	}
}
