package com.pchudzik.springmock.infrastructure.definition.registry;

import com.pchudzik.springmock.infrastructure.MockConstants;
import com.pchudzik.springmock.infrastructure.definition.DoubleDefinition;
import com.pchudzik.springmock.infrastructure.definition.MockDefinition;
import com.pchudzik.springmock.infrastructure.definition.SpyDefinition;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Registry which holds all mocks and spies defined for test context.
 */
public class DoubleRegistry {
	public static final String BEAN_NAME = MockConstants.PACKAGE_PREFIX + "doubleDefinitionRegistry";

	private final Collection<MockDefinition> mocks;
	private final Collection<SpyDefinition> spies;

	public DoubleRegistry(Collection<MockDefinition> mocks, Collection<SpyDefinition> spies) {
		this.mocks = new HashSet<>(mocks);
		this.spies = new HashSet<>(spies);
	}

	public Collection<String> getRegisteredMockNames() {
		return getMocks().stream()
				.map(DoubleDefinition::getName)
				.collect(Collectors.toSet());
	}

	public Collection<MockDefinition> getMocks() {
		return Collections.unmodifiableCollection(mocks);
	}

	public Collection<SpyDefinition> getSpies() {
		return Collections.unmodifiableCollection(spies);
	}

	public DoubleSearch<SpyDefinition> spySearch() {
		return new DoubleSearch<>(getSpies());
	}

	public DoubleSearch<MockDefinition> mockSearch() {
		return new DoubleSearch<>(getMocks());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final DoubleRegistry that = (DoubleRegistry) o;
		return Objects.equals(mocks, that.mocks) &&
				Objects.equals(spies, that.spies);
	}

	@Override
	public int hashCode() {
		return Objects.hash(mocks, spies);
	}
}
