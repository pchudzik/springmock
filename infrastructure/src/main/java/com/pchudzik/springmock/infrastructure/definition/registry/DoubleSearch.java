package com.pchudzik.springmock.infrastructure.definition.registry;

import com.pchudzik.springmock.infrastructure.definition.DoubleDefinition;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

public class DoubleSearch {
	private final Collection<DoubleDefinition> doubles;

	DoubleSearch(Collection<DoubleDefinition> doubles) {
		this.doubles = doubles;
	}

	public boolean containsAnyDoubleMatching(String beaName, Class<?> beanClass) {
		return 1 <= matchingDoublesCount(filterByName(beaName).or(filterByClass(beanClass)));
	}

	public boolean containsExactlyOneDouble(String beanName, Class<?> beanClass) {
		return 1 == matchingDoublesCount(filterByName(beanName).and(filterByClass(beanClass)));
	}

	public boolean containsExactlyOneDouble(String beanName) {
		return 1 == matchingDoublesCount(filterByName(beanName));
	}

	public boolean containsExactlyOneDouble(Class<?> beanClass) {
		return 1 == matchingDoublesCount(filterByClass(beanClass));
	}

	public DoubleDefinition findOneDefinition(String beanName, Class<?> beanClass) {
		final List<DoubleDefinition> matchingDoubles = matchingDoubles(filterByName(beanName).and(filterByClass(beanClass)));
		ensureSingleMatch(matchingDoubles, "[name = " + beanName + " AND class = " + beanClass + "]");
		return matchingDoubles.get(0);
	}

	public DoubleDefinition findOneDefinition(String beanName) {
		final List<DoubleDefinition> matchingDoubles = matchingDoubles(filterByName(beanName));
		ensureSingleMatch(matchingDoubles, "[name = " + beanName + "]");
		return matchingDoubles.get(0);
	}

	public DoubleDefinition findOneDefinition(Class<?> beanClass) {
		final List<DoubleDefinition> matchingDoubles = matchingDoubles(filterByClass(beanClass));
		ensureSingleMatch(matchingDoubles, "[class = "+ beanClass + "]");
		return matchingDoubles.get(0);
	}

	private int matchingDoublesCount(Predicate<DoubleDefinition> matcher) {
		return matchingDoubles(matcher).size();
	}

	private List<DoubleDefinition> matchingDoubles(Predicate<DoubleDefinition> matcher) {
		return doubles.stream().filter(matcher).collect(toList());
	}

	private static void ensureSingleMatch(List<DoubleDefinition> matchingDoubles, String filterDescription) {
		if(matchingDoubles.isEmpty()) {
			throw new IllegalStateException("Expected exactly one double matching " + filterDescription + " but found none");
		}
		if(matchingDoubles.size() > 1) {
			throw new IllegalStateException("Expected exactly one double matching " + filterDescription + " but found multiple doubles " + matchingDoubles);
		}
	}

	private static Predicate<DoubleDefinition> filterByName(String beaName) {
		return doubleDefinition -> doubleDefinition.hasNameOrAlias(beaName);
	}

	private static Predicate<DoubleDefinition> filterByClass(Class<?> beanClass) {
		return doubleDefinition -> doubleDefinition.hasClass(beanClass);
	}
}
