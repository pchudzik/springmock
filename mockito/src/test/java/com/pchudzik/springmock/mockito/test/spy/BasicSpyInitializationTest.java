package com.pchudzik.springmock.mockito.test.spy;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import com.pchudzik.springmock.mockito.configuration.MockitoDouble;
import com.pchudzik.springmock.mockito.test.spy.infrastructure.Service;
import com.pchudzik.springmock.mockito.test.spy.infrastructure.ServiceInteractionRecorder;
import com.pchudzik.springmock.mockito.test.spy.infrastructure.SpyConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.stubbing.answers.Returns;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.Closeable;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mockingDetails;
import static org.springframework.util.Assert.isAssignable;

@RunWith(SpringRunner.class)
public class BasicSpyInitializationTest {
	@AutowiredSpy
	Service service;

	@AutowiredSpy
	@MockitoDouble(defaultAnswer = ReturnsHelloSpy.class)
	Service withConfigurationMock;

	@AutowiredMock
	@MockitoDouble(extraInterfaces = Closeable.class)
	Service withExtraInterface;

	@Autowired
	ServiceInteractionRecorder interactionRecorder;

	@Test
	public void should_inject_spy() {
		assertTrue(mockingDetails(service).isSpy());
	}

	@Test
	public void should_replace_bean_with_spy() {
		//given
		final String methodArgument = "spy";
		final String methodResponse = "spy response";
		Mockito.when(service.hello(methodArgument)).thenReturn(methodResponse);

		//when
		final String result = service.hello(methodArgument);

		//then
		assertEquals(methodResponse, result);
		assertEquals(singletonList(methodArgument), interactionRecorder.getInteractions());
	}

	@Configuration
	@Import(SpyConfig.class)
	static class Config {
		@Bean
		public Service withConfigurationMock() {
			return new Service(new ServiceInteractionRecorder());
		}

		@Bean
		public Service withExtraInterface() {
			return new Service(new ServiceInteractionRecorder());
		}
	}

	@Test
	public void should_configure_default_answer_for_mock() {
		assertEquals(
				ReturnsHelloSpy.RESPONSE,
				withConfigurationMock.hello("any argument"));
	}

	@Test
	public void should_implement_additional_interface() {
		isAssignable(
				Closeable.class,
				withExtraInterface.getClass());
	}

	private static class ReturnsHelloSpy extends Returns {
		public static final String RESPONSE = "hello spy!!1";

		public ReturnsHelloSpy() {
			super(RESPONSE);
		}
	}
}
