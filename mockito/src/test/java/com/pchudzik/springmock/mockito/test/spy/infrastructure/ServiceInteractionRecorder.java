package com.pchudzik.springmock.mockito.test.spy.infrastructure;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ServiceInteractionRecorder {
	private final LinkedList<String> interactions = new LinkedList<>();

	public void record(String argument) {
		this.interactions.add(argument);
	}

	public List<String> getInteractions() {
		return Collections.unmodifiableList(interactions);
	}
}
