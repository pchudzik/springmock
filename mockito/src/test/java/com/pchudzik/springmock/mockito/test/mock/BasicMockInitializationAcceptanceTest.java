package com.pchudzik.springmock.mockito.test.mock;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.mockito.configuration.MockitoDouble;
import com.pchudzik.springmock.mockito.test.mock.infrastructure.AnyService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.stubbing.answers.Returns;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.Closeable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.util.Assert.isAssignable;

@RunWith(SpringRunner.class)
public class BasicMockInitializationAcceptanceTest {
	@AutowiredMock(name = "anyService")
	AnyService anyService;

	@AutowiredMock
	@MockitoDouble(defaultAnswer = ReturnsHelloMock.class)
	AnyService withConfigurationMock;

	@AutowiredMock
	@MockitoDouble(extraInterfaces = Closeable.class)
	AnyService withExtraInterface;

	@Test
	public void should_inject_mock() {
		assertTrue(mockingDetails(anyService).isMock());
	}

	@Test
	public void should_inject_working_mock() {
		//given
		when(anyService.hello()).thenReturn("hello");

		//when
		final String result = anyService.hello();

		//then
		assertEquals("hello", result);
		verify(anyService).hello();
	}

	@Test
	public void should_configure_default_answer_for_mock() {
		assertEquals(
				ReturnsHelloMock.RESPONSE,
				withConfigurationMock.hello());
	}

	@Test
	public void should_implement_additional_interface() {
		isAssignable(
				Closeable.class,
				withExtraInterface.getClass());
	}

	private static class ReturnsHelloMock extends Returns {
		public static final String RESPONSE = "hello mock!!1";

		public ReturnsHelloMock() {
			super(RESPONSE);
		}
	}
}
