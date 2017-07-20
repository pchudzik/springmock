package com.pchudzik.springmock.infrastructure.definition;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.*;

class DoubleName {
	private final String name;
	private final Set<String> aliases;

	DoubleName(String name, Collection<String> aliases) {
		this.name = name;
		this.aliases = new HashSet<>(aliases);
	}

	public String getName() {
		return name;
	}

	public Collection<String> getAliases() {
		return Collections.unmodifiableCollection(aliases);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final DoubleName that = (DoubleName) o;

		return Objects.equals(name, that.name) &&
				Objects.equals(aliases, that.aliases);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, aliases);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("name", name)
				.append("aliases", aliases)
				.build();
	}

	public boolean hasName(String beanName) {
		return this.name.equals(beanName) || this.aliases.contains(beanName);
	}
}
