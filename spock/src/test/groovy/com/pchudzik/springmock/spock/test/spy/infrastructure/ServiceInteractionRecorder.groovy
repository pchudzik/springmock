package com.pchudzik.springmock.spock.test.spy.infrastructure

class ServiceInteractionRecorder {
	private LinkedList<String> interactions = []

	void record(String argument) {
		this.interactions.add(argument)
	}

	List<String> getInteractions() {
		return Collections.unmodifiableList(interactions)
	}

	String getLatestInteraction() {
		return interactions[0]
	}

	void resetLatestInteraction() {
		interactions.removeLast()
	}

	void resetInteractions() {
		interactions = []
	}
}
