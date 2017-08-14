package com.pchudzik.springmock.mockito.test.mock;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.mockito.test.mock.infrastructure.AnyService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mockingDetails;

@RunWith(SpringRunner.class)
public class ShouldReuseMocksInjectedInConfigurationClass {
	@Autowired
	Config config;

	@AutowiredMock
	AnyService anyService;

	@Test
	public void should_reuse_mocks_in_configuration_and_test_class() {
		assertTrue(mockingDetails(anyService).isMock());
		assertSame(
				config.service,
				anyService);


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
