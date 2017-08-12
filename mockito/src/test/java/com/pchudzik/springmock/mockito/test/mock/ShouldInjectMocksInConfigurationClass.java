package com.pchudzik.springmock.mockito.test.mock;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.mockito.test.mock.infrastructure.AnyService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;

import static org.mockito.Mockito.mockingDetails;

@RunWith(SpringRunner.class)
public class ShouldInjectMocksInConfigurationClass {
	@Test
	public void should_setup_context() {
		//pass
	}

	@Configuration
	static class Config {
		@AutowiredMock
		AnyService service;

		@PostConstruct
		public void bean() {
			Assert.assertTrue(mockingDetails(service).isMock());
		}
	}
}
