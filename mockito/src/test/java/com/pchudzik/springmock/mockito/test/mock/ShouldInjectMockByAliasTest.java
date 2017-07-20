package com.pchudzik.springmock.mockito.test.mock;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.mockito.test.mock.infrastructure.AnyService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mockingDetails;

@RunWith(SpringRunner.class)
public class ShouldInjectMockByAliasTest {
	private static final String ALIAS_NAME = "alias";

	@AutowiredMock(alias = ALIAS_NAME)
	AnyService mock;

	@AutowiredMock
	AnyService otherMock;

	@Autowired
	Service service;

	@Test
	public void should_inject_mock_by_alias() {
		assertSame(mock, service.getService());
		assertTrue(mockingDetails(service.getService()).isMock());
	}

	@Configuration
	static class Config {
		@Bean
		Service service() {
			return new Service();
		}
	}

	private static class Service {
		@Autowired
		@Qualifier(ALIAS_NAME)
		private AnyService service;

		public AnyService getService() {
			return service;
		}
	}
}
