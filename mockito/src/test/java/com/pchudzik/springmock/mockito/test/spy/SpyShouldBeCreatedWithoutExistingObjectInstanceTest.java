package com.pchudzik.springmock.mockito.test.spy;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
public class SpyShouldBeCreatedWithoutExistingObjectInstanceTest {
	@AutowiredSpy
	Service service;

	@Test
	public void should_inject_spy_when_no_backing_object_present() {
		assertEquals(
				Service.DEFAULT_RESPONSE,
				service.hello());

		Mockito
				.verify(service)
				.hello();
	}


	class Service {
		public static final String DEFAULT_RESPONSE = "hello";

		public String hello() {
			return DEFAULT_RESPONSE;
		}
	}
}
