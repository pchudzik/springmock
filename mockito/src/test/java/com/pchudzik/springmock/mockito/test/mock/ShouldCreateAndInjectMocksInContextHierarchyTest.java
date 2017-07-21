package com.pchudzik.springmock.mockito.test.mock;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.mockito.test.mock.infrastructure.AnyService;
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
		@ContextConfiguration(classes = ShouldCreateAndInjectMocksInContextHierarchyTest.ChildConfig.class),
		@ContextConfiguration(classes = ShouldCreateAndInjectMocksInContextHierarchyTest.ParentConfig.class)
})
public class ShouldCreateAndInjectMocksInContextHierarchyTest {
	@AutowiredMock
	ChildService childService;

	@Autowired
	ParentService parentService;

	@Test
	public void should_create_mocks_in_hierarchical_configuration() {
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
		AnyService anyService() {
			return () -> "any service";
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
