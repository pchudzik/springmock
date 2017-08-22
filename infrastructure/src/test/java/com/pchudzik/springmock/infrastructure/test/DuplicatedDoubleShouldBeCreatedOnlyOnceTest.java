package com.pchudzik.springmock.infrastructure.test;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import com.pchudzik.springmock.infrastructure.definition.DoubleDefinition;
import com.pchudzik.springmock.infrastructure.test.infrastructure.MockitoBasedDoubleFactory;
import com.pchudzik.springmock.infrastructure.test.infrastructure.SpringMockContextBootstrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.times;

@RunWith(SpringRunner.class)
@BootstrapWith(DuplicatedDoubleShouldBeCreatedOnlyOnceTest.TestContextBootstrap.class)
@AutowiredMock(doubleClass = DuplicatedDoubleShouldBeCreatedOnlyOnceTest.MockService.class)
@AutowiredSpy(doubleClass = DuplicatedDoubleShouldBeCreatedOnlyOnceTest.SpyService.class)
public class DuplicatedDoubleShouldBeCreatedOnlyOnceTest {
	@Autowired
	MockitoBasedDoubleFactory doubleFactory;

	@AutowiredMock
	MockService mockService;

	@AutowiredSpy
	SpyService spyService;

	@Test
	public void mock_should_be_created_only_once() {
		Mockito
				.verify(doubleFactory.getMockedDoubleFactory(), times(1))
				.createMock(any(DoubleDefinition.class));
	}

	@Test
	public void spy_should_be_created_only_once() {
		Mockito
				.verify(doubleFactory.getMockedDoubleFactory(), times(1))
				.createSpy(anyObject(), any(DoubleDefinition.class));
	}

	@Configuration
	@AutowiredMock(doubleClass = MockService.class)
	@AutowiredSpy(doubleClass = SpyService.class)
	static class Config {
	}

	static class TestContextBootstrap extends SpringMockContextBootstrapper {
		protected TestContextBootstrap() {
			super(() -> MockitoBasedDoubleFactory.builder()
					.mock(Mockito.mock(MockService.class))
					.spy(Mockito.mock(SpyService.class))
					.build());
		}
	}

	static class MockService {
	}

	static class SpyService {
	}
}
