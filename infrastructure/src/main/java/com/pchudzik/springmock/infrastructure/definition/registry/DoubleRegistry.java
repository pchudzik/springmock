package com.pchudzik.springmock.infrastructure.definition.registry;

import com.pchudzik.springmock.infrastructure.MockConstants;
import com.pchudzik.springmock.infrastructure.definition.DoubleDefinition;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Registry which holds all mocks and spies defined for test context.
 */
public class DoubleRegistry {
	public static final String BEAN_NAME = MockConstants.PACKAGE_PREFIX + "doubleDefinitionRegistry";

	private final Collection<DoubleDefinition> mocks;
	private final Collection<DoubleDefinition> spies;

	public DoubleRegistry(Collection<DoubleDefinition> mocks, Collection<DoubleDefinition> spies) {
		this.mocks = new HashSet<>(mocks);
		this.spies = new HashSet<>(spies);
	}

	public Collection<String> getRegisteredDoubleNames() {
		return Stream
				.concat(
						getMocks().stream(),
						getSpies().stream())
				.map(DoubleDefinition::getName)
				.collect(Collectors.toSet());
	}

	public Collection<DoubleDefinition> getMocks() {
		return Collections.unmodifiableCollection(mocks);
	}

	public Collection<DoubleDefinition> getSpies() {
		return Collections.unmodifiableCollection(spies);
	}

	public DoubleSearch spySearch() {
		return new DoubleSearch(getSpies());
	}

	public DoubleSearch mockSearch() {
		return new DoubleSearch(getMocks());
	}

	public DoubleSearch doublesSearch() {
		return new DoubleSearch(Stream
				.concat(getMocks().stream(), getSpies().stream())
				.collect(Collectors.toList()));
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
