package com.pchudzik.springmock.infrastructure.definition;

import com.pchudzik.springmock.infrastructure.definition.registry.DoubleRegistry;
import org.junit.Test;

import java.util.Collection;

import static com.pchudzik.springmock.infrastructure.definition.DoubleDefinitionTestFactory.doubleDefinition;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class DoubleRegistryTest {

	@Test
	public void should_return_all_registered_mock_names() {
		//given
		final String firstMockName = "firstService";
		final String secondMockName = "secondService";
		final String firstSpyName = "spy1";
		final String secondSpyName = "spy2";
		final DoubleRegistry registry = new DoubleRegistry(
				asList(
						doubleDefinition(Service.class, firstMockName),
						doubleDefinition(OtherService.class, secondMockName)),
				asList(
						doubleDefinition(Service.class, firstSpyName),
						doubleDefinition(Service.class, secondSpyName)));

		//when
		final Collection<String> mockNames = registry.getRegisteredDoubleNames();

		//then
		assertEquals(4, mockNames.size());
		assertThat(mockNames, containsInAnyOrder(firstMockName, secondMockName, firstSpyName, secondSpyName));
	}

	private static class Service {
	}

	private static class OtherService {
	}
}