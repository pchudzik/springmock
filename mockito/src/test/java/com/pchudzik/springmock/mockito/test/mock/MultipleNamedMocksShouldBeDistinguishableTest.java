package com.pchudzik.springmock.mockito.test.mock;

import com.pchudzik.springmock.mockito.test.mock.infrastructure.AnyService;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertSame;

@RunWith(SpringRunner.class)
public class MultipleNamedMocksShouldBeDistinguishableTest {
	@AutowiredMock(name = "otherService")
	AnyService otherService;

	@AutowiredMock(name = "myService")
	AnyService myService;

	@Autowired
	Service service;

	@Test
	public void should_inject_named_mock_instance_to_all_object_instances() {
		assertSame(myService, service.getAnyService());
	}

	@Configuration
	static class Config {
		@Bean
		Service service(@Autowired @Qualifier("myService") AnyService anyService) {
			return new Service(anyService);
		}
	}

	static class Service {
		private final AnyService anyService;

		public Service(AnyService anyService) {
			this.anyService = anyService;
		}

		public AnyService getAnyService() {
			return anyService;
		}
	}
}
