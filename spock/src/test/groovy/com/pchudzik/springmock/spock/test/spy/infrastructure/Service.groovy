package com.pchudzik.springmock.spock.test.spy.infrastructure

class Service {
	public static final String DEFAULT_RESPONSE = "not a spy"
	private final ServiceInteractionRecorder serviceInteractionRecorder

	Service(ServiceInteractionRecorder serviceInteractionRecorder) {
		this.serviceInteractionRecorder = serviceInteractionRecorder
	}

	String hello(String argument) {
		serviceInteractionRecorder.record(argument);
		return DEFAULT_RESPONSE;
	}

	ServiceInteractionRecorder getServiceInteractionRecorder() {
		return serviceInteractionRecorder
	}
}
