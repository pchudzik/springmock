package com.pchudzik.springmock.mockito.test.spy;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import com.pchudzik.springmock.mockito.test.spy.infrastructure.InteractionRecorderConfig;
import com.pchudzik.springmock.mockito.test.spy.infrastructure.Service;
import com.pchudzik.springmock.mockito.test.spy.infrastructure.ServiceInteractionRecorder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
public class ShouldInjectSpyByNameTest {
	@AutowiredSpy(name = Config.SPY_NAME)
	@Qualifier(Config.SPY_NAME)
	Service spyService;

	@Autowired
	@Qualifier(Config.NOT_SPY)
	Service notSpy;

	@Test
	public void should_create_and_inject_spy_by_name() {
		assertTrue(Mockito.mockingDetails(spyService).isSpy());
		assertFalse(Mockito.mockingDetails(notSpy).isSpy());
	}

	@Configuration
	@Import(InteractionRecorderConfig.class)
	static class Config {
		public static final String NOT_SPY = "notSpyService";
		public static final String SPY_NAME = "serviceToSpyOn";

		@Autowired
		ServiceInteractionRecorder serviceInteractionRecorder;

		@Bean(NOT_SPY)
		Service service() {
			return new Service(new ServiceInteractionRecorder());
		}

		@Bean(SPY_NAME)
		Service spy() {
			return new Service(serviceInteractionRecorder);
		}
	}
}
