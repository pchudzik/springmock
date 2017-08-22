package com.pchudzik.springmock.infrastructure.test;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import com.pchudzik.springmock.infrastructure.test.infrastructure.FixedDoubleFactory;
import com.pchudzik.springmock.infrastructure.test.infrastructure.SpringMockContextBootstrapper;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

@Ignore("https://github.com/pchudzik/springmock/issues/8")
@RunWith(SpringRunner.class)
@BootstrapWith(SpyAndMockOfTheSameClassShouldBeDistinguishableTest.TestContextBootstrap.class)
public class SpyAndMockOfTheSameClassShouldBeDistinguishableTest {
	private static final MyService aMock = Mockito.mock(MyService.class, "a mock");
	private static final MyService aSpy = Mockito.mock(MyService.class, "a spy");
	private static final FixedDoubleFactory doubleFactory = FixedDoubleFactory.builder()
			.mock(aMock)
			.spy(aSpy)
			.build();

	@AutowiredSpy
	MyService spy;

	@AutowiredMock
	MyService mock;

	@Test
	public void spy_should_be_distinguishable_from_mock_of_the_same_class() {
		assertNotSame(spy, mock);
	}

	@Test
	public void should_distinguish_mock() {
		assertSame(aMock, mock);
	}

	@Test
	public void should_distinguish_spy() {
		assertSame(aSpy, spy);
	}

	static class MyService {
	}

	static class TestContextBootstrap extends SpringMockContextBootstrapper {

		protected TestContextBootstrap() {
			super(() -> doubleFactory);
		}
	}
}
