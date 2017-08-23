package com.pchudzik.springmock.infrastructure.definition;

import org.apache.commons.lang3.StringUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.Collection;
import java.util.HashSet;

public class DoubleDefinitionMatchers {
	public static Matcher<DoubleDefinition> doubleWithName(String definitionName) {
		return new BaseMatcher<DoubleDefinition>() {
			@Override
			public boolean matches(Object item) {
				final DoubleDefinition definition = (DoubleDefinition) item;
				return definitionName.equals(definition.getName());
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("name ").appendText(definitionName);
			}
		};
	}

	public static Matcher<DoubleDefinition> doubleForClass(Class<?> doubleClass) {
		return new BaseMatcher<DoubleDefinition>() {
			@Override
			public boolean matches(Object item) {
				final DoubleDefinition definition = (DoubleDefinition) item;
				return doubleClass.equals(definition.getDoubleClass());
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("doubleClass ").appendText(doubleClass.getCanonicalName());
			}
		};
	}

	public static Matcher<DoubleDefinition> doubleWithAliases(Collection<String> aliases) {
		return new BaseMatcher<DoubleDefinition>() {
			@Override
			public boolean matches(Object item) {
				final DoubleDefinition definition = (DoubleDefinition) item;
				return definition.getAliases().size() == aliases.size() &&
						new HashSet<>(aliases).equals(new HashSet<>(definition.getAliases()));
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("doubleAliases ").appendText(StringUtils.join(aliases, ", "));
			}
		};
	}

	public static Matcher<DoubleDefinition> doubleWithConfiguration(Object configuration) {
		return new BaseMatcher<DoubleDefinition>() {
			@Override
			public boolean matches(Object item) {
				final DoubleDefinition definition = (DoubleDefinition) item;
				return definition.getConfiguration(configuration.getClass()) == configuration;
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("doubleConfiguration ").appendText(configuration.toString());
			}
		};
	}
}
