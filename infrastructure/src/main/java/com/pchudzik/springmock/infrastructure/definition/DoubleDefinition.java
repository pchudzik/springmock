package com.pchudzik.springmock.infrastructure.definition;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Collection;
import java.util.Objects;

public abstract class DoubleDefinition {
	private final Class<?> doubleClass;
	private final DoubleName doubleName;

	public DoubleDefinition(Class<?> doubleClass, String name, Collection<String> aliases) {
		this.doubleClass = doubleClass;
		this.doubleName = new DoubleName(name, aliases);
	}

	public Class<?> getDoubleClass() {
		return doubleClass;
	}

	public String getName() {
		return doubleName.getName();
	}

	public Collection<String> getAliases() {
		return doubleName.getAliases();
	}

	public boolean hasNameOrAlias(String beanName) {
		return doubleName.hasName(beanName);
	}


	public boolean hasClass(Class<?> spyClass) {
		return spyClass.isAssignableFrom(doubleClass);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final DoubleDefinition that = (DoubleDefinition) o;

		return Objects.equals(doubleClass, that.doubleClass) &&
				Objects.equals(doubleName, that.doubleName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(doubleClass, doubleName);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("doubleClass", doubleClass)
				.append("doubleName", doubleName)
				.toString();
	}
}
