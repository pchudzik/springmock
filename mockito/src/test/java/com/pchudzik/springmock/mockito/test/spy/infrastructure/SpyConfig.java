package com.pchudzik.springmock.mockito.test.spy.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(InteractionRecorderConfig.class)
public class SpyConfig {
	private final ServiceInteractionRecorder interactionRecorder;

	public SpyConfig(ServiceInteractionRecorder interactionRecorder) {
		this.interactionRecorder = interactionRecorder;
	}

	@Bean
	Service service() {
		return new Service(interactionRecorder);
	}
}
