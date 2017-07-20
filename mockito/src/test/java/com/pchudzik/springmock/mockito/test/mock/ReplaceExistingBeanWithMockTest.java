package com.pchudzik.springmock.mockito.test.mock;

import com.pchudzik.springmock.mockito.test.mock.infrastructure.AnyService;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class ReplaceExistingBeanWithMockTest {
	@AutowiredMock
	AnyService anyService;

	@Test
	public void should_replace_existing_bean_with_mock() {
		//given
		Mockito.when(anyService.hello()).thenReturn("mock");

		//expect
		Assert.assertEquals(anyService.hello(), "mock");
	}

	@Configuration
	static class Config {
		@Bean
		AnyService anyService() {
			return () -> "not a mock";
		}
	}
}
