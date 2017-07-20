package com.pchudzik.springmock.infrastructure.definition;

import com.pchudzik.springmock.infrastructure.definition.registry.DoubleRegistry;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class DoubleRegistryTest {
	private Collection<SpyDefinition> noSpies = Collections.emptyList();

	@Test
	public void should_return_all_registered_mock_names() {
		//given
		final String firstMockName = "firstService";
		final String secondMockName = "secondService";
		final DoubleRegistry registry = new DoubleRegistry(asList(
				new MockDefinition(Service.class, firstMockName, Collections.emptyList()),
				new MockDefinition(OtherService.class, secondMockName, Collections.emptyList())),
				noSpies);

		//when
		final Collection<String> mockNames = registry.getRegisteredMockNames();

		//then
		assertEquals(2, mockNames.size());
		assertThat(mockNames, Matchers.containsInAnyOrder(firstMockName, secondMockName));
	}

	private static class Service {
	}

	private static class OtherService {
	}
}