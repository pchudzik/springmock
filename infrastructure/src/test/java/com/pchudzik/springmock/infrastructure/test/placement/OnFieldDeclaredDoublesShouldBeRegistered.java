package com.pchudzik.springmock.infrastructure.test.placement;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import com.pchudzik.springmock.infrastructure.test.infrastructure.MatchingDoubleFactory;
import com.pchudzik.springmock.infrastructure.test.infrastructure.SpringMockContextBootstrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@BootstrapWith(OnFieldDeclaredDoublesShouldBeRegistered.TestBootstrapper.class)
public class OnFieldDeclaredDoublesShouldBeRegistered {
	private static final MockService aMock = Mockito.mock(MockService.class);
	private static final SpyService aSpy = Mockito.mock(SpyService.class);
	private static final PartialService aPartialMock = Mockito.mock(PartialService.class);

	private static final String MOCK = "mockService";
	private static final String SPY = "spyService";
	private static final String PARTIAL_MOCK = "partialMock";

	@AutowiredMock
	MockService mockService;

	@AutowiredSpy
	SpyService spyService;

	@AutowiredSpy
	PartialService partialMock;

	@Test
	public void should_inject_created_mock() {
		assertNotNull(mockService);
		assertEquals(
				aMock,
				mockService);
	}

	@Test
	public void should_inject_created_spy() {
		assertNotNull(spyService);
		assertEquals(aSpy, spyService);
	}

	@Test
	public void should_inject_partial_spy() {
		assertNotNull(partialMock);
		assertEquals(aPartialMock, partialMock);
	}

	@Configuration
	static class Config {
		@Bean(name = SPY)
		SpyService spyService() {
			return new SpyService();
		}
	}

	static class TestBootstrapper extends SpringMockContextBootstrapper {
		public TestBootstrapper() {
			super(() -> MatchingDoubleFactory.builder()
					.mock(aMock, def -> MOCK.equals(def.getName()))
					.spy(aSpy, (bean, def) -> SPY.equals(def.getName()))
					.spy(aPartialMock, (bean, def) -> PARTIAL_MOCK.equals(def.getName()))
					.build());
		}
	}

	private interface MockService {
	}

	private static class SpyService {
	}

	private static class PartialService {
	}
}
