package com.pchudzik.springmock.mockito.test.spy.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InteractionRecorderConfig {
	@Bean
	ServiceInteractionRecorder interactionRecorder() {
		return new ServiceInteractionRecorder();
	}
}
