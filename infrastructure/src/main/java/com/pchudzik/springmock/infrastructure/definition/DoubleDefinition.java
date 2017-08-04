package com.pchudzik.springmock.infrastructure.definition;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class DoubleDefinition {
	public static final Object NO_CONFIGURATION = null;

	private final Class<?> doubleClass;
	private final DoubleName doubleName;
	private final Object doubleConfiguration;

	protected DoubleDefinition(Class<?> doubleClass, Object doubleConfiguration, DoubleName doubleName) {
		this.doubleClass = doubleClass;
		this.doubleName = doubleName;
		this.doubleConfiguration = doubleConfiguration;
	}

	public static DoubleDefinitionBuilder builder() {
		return new DoubleDefinitionBuilder();
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

	public <T> T getConfiguration(Class<T> configurationClass) {
		if (doubleConfiguration == null) {
			return null;
		}

		Assert.isInstanceOf(configurationClass, doubleConfiguration);
		return (T) doubleConfiguration;
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

		return Objects.equals(this.doubleClass, that.doubleClass) &&
				Objects.equals(this.doubleName, that.doubleName);
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

	public static class DoubleDefinitionBuilder {
		private Class<?> doubleClass;
		private String name;
		private Collection<String> aliases = Collections.emptyList();
		private Object doubleConfiguration = NO_CONFIGURATION;

		private DoubleDefinitionBuilder() {
		}

		public DoubleDefinitionBuilder doubleClass(Class<?> doubleClass) {
			this.doubleClass = doubleClass;
			return this;
		}

		public DoubleDefinitionBuilder name(String name) {
			this.name = name;
			return this;
		}

		public DoubleDefinitionBuilder aliases(Collection<String> aliases) {
			this.aliases = aliases;
			return this;
		}

		public DoubleDefinitionBuilder doubleConfiguration(Object doubleConfiguration) {
			this.doubleConfiguration = doubleConfiguration;
			return this;
		}

		public DoubleDefinition build() {
			Assert.notNull(doubleClass);
			Assert.notNull(name);

			return new DoubleDefinition(doubleClass, doubleConfiguration, new DoubleName(name, aliases));
		}
	}
}
