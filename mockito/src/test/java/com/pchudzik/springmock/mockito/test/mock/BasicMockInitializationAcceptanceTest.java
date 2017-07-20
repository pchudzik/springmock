package com.pchudzik.springmock.mockito.test.mock;

import com.pchudzik.springmock.mockito.test.mock.infrastructure.AnyService;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class BasicMockInitializationAcceptanceTest {
	@AutowiredMock(name = "anyService")
	AnyService anyService;

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

}
