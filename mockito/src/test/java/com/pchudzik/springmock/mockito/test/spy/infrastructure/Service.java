package com.pchudzik.springmock.mockito.test.spy.infrastructure;

public class Service {
	public static final String DEFAULT_RESPONSE = "not a spy";
	private final ServiceInteractionRecorder serviceInteractionRecorder;

	public Service(ServiceInteractionRecorder serviceInteractionRecorder) {
		this.serviceInteractionRecorder = serviceInteractionRecorder;
	}

	public String hello(String argument) {
		serviceInteractionRecorder.record(argument);
		return DEFAULT_RESPONSE;
	}
}
