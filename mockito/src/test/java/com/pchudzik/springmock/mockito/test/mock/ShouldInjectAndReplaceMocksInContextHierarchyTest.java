package com.pchudzik.springmock.mockito.test.mock;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringRunner;

import static com.pchudzik.springmock.mockito.test.mock.ShouldInjectAndReplaceMocksInContextHierarchyTest.ParentService.PARENT_PREFIX_RESPONSE;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@ContextHierarchy({
		@ContextConfiguration(classes = ShouldInjectAndReplaceMocksInContextHierarchyTest.ChildConfig.class),
		@ContextConfiguration(classes = ShouldInjectAndReplaceMocksInContextHierarchyTest.ParentConfig.class)
})
public class ShouldInjectAndReplaceMocksInContextHierarchyTest {
	@AutowiredMock
	ChildService childService;

	@Autowired
	ParentService parentService;

	@Test
	public void should_replace_beans_with_mocks_in_hierarchical_configuration() {
		//given
		final String mockResponse = "mocked child";
		Mockito
				.when(childService.hello())
				.thenReturn(mockResponse);

		//when
		final String result = parentService.hello();

		//then
		assertEquals(
				PARENT_PREFIX_RESPONSE + mockResponse,
				result);
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
		String hello() {
			return "child";
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
