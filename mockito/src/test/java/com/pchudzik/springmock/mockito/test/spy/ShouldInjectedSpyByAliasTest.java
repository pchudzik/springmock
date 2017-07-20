package com.pchudzik.springmock.mockito.test.spy;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import com.pchudzik.springmock.mockito.test.spy.infrastructure.Service;
import com.pchudzik.springmock.mockito.test.spy.infrastructure.ServiceInteractionRecorder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mockingDetails;

@RunWith(SpringRunner.class)
public class ShouldInjectedSpyByAliasTest {
	private static final String ALIAS = "alias";

	@AutowiredSpy(name="spy name", alias = ALIAS)
	Service spy;

	@Autowired
	OtherService otherService;

	@Test
	public void should_inject_spy_by_alias() {
		assertSame(spy, otherService.getService());
		assertTrue(mockingDetails(spy).isSpy());
	}

	@Configuration
	static class Config {
		@Bean
		OtherService otherService() {
			return new OtherService();
		}

		@Bean(name = ALIAS)
		Service service() {
			return new Service(new ServiceInteractionRecorder());
		}
	}

	private static class OtherService {
		@Autowired
		Service service;

		public Service getService() {
			return service;
		}
	}
}
