package com.pchudzik.springmock.spock.test.spy.infrastructure

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class InteractionRecorderConfig {
	@Bean
	ServiceInteractionRecorder interactionRecorder() {
		new ServiceInteractionRecorder()
	}
}
