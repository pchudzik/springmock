package com.pchudzik.springmock.spock.test.spy.infrastructure

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(InteractionRecorderConfig.class)
class SpyConfig {
	private final ServiceInteractionRecorder interactionRecorder

	SpyConfig(ServiceInteractionRecorder interactionRecorder) {
		this.interactionRecorder = interactionRecorder
	}

	@Bean
	Service service() {
		new Service(interactionRecorder)
	}
}
