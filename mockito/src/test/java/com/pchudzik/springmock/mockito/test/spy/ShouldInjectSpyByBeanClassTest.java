package com.pchudzik.springmock.mockito.test.spy;

import com.pchudzik.springmock.mockito.test.spy.infrastructure.Service;
import com.pchudzik.springmock.mockito.test.spy.infrastructure.ServiceInteractionRecorder;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import com.pchudzik.springmock.mockito.test.spy.infrastructure.InteractionRecorderConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class ShouldInjectSpyByBeanClassTest {
	@AutowiredSpy(name = "name match is not necessary for mock injection")
	Service service;

	@Test
	public void should_inject_spy() {
		Assert.assertTrue(Mockito.mockingDetails(service).isSpy());
	}

	@Configuration
	@Import(InteractionRecorderConfig.class)
	static class Config {
		private final ServiceInteractionRecorder interactionRecorder;

		Config(ServiceInteractionRecorder interactionRecorder) {
			this.interactionRecorder = interactionRecorder;
		}

		@Bean("customServiceName")
		Service service() {
			return new Service(interactionRecorder);
		}
	}
}
