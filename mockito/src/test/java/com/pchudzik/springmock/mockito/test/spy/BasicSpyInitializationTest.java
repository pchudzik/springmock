package com.pchudzik.springmock.mockito.test.spy;

import com.pchudzik.springmock.mockito.test.spy.infrastructure.Service;
import com.pchudzik.springmock.mockito.test.spy.infrastructure.ServiceInteractionRecorder;
import com.pchudzik.springmock.mockito.test.spy.infrastructure.SpyConfig;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mockingDetails;

@RunWith(SpringRunner.class)
public class BasicSpyInitializationTest {
	@AutowiredSpy
	Service service;

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
	static class Config {}
}
