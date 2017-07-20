package com.pchudzik.springmock.mockito.test.mock;

import com.pchudzik.springmock.mockito.test.mock.infrastructure.AnyService;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mockingDetails;

@RunWith(SpringRunner.class)
public class MockDependenciesShouldBeSkippedFromAutowiringTest {
	@AutowiredMock
	DestinationService service;

	@Test
	public void should_inject_mock_and_skip_dependency_injection() {
		//expect
		assertTrue(mockingDetails(service).isMock());
	}

	static class DestinationService {
		@Autowired
		private AnyService anyService;

		public String toStuff() {
			return anyService.hello();
		}
	}
}
