package com.pchudzik.springmock.infrastructure.definition.registry;

import com.pchudzik.springmock.infrastructure.definition.DoubleDefinition;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

public class DoubleSearch<T extends DoubleDefinition> {
	private final Collection<T> doubles;

	DoubleSearch(Collection<T> doubles) {
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

	public T findOneDefinition(String beanName, Class<?> beanClass) {
		final List<T> matchingDoubles = matchingDoubles(filterByName(beanName).and(filterByClass(beanClass)));
		ensureSingleMatch(matchingDoubles, "[name = " + beanName + " AND class = " + beanClass + "]");
		return matchingDoubles.get(0);
	}

	public T findOneDefinition(String beanName) {
		final List<T> matchingDoubles = matchingDoubles(filterByName(beanName));
		ensureSingleMatch(matchingDoubles, "[name = " + beanName + "]");
		return matchingDoubles.get(0);
	}

	public T findOneDefinition(Class<?> beanClass) {
		final List<T> matchingDoubles = matchingDoubles(filterByClass(beanClass));
		ensureSingleMatch(matchingDoubles, "[class = "+ beanClass + "]");
		return matchingDoubles.get(0);
	}

	private void ensureSingleMatch(List<T> matchingDoubles, String filterDescription) {
		if(matchingDoubles.isEmpty()) {
			throw new IllegalStateException("Expected exactly one double matching " + filterDescription + " but found none");
		}
		if(matchingDoubles.size() > 1) {
			throw new IllegalStateException("Expected exactly one double matching " + filterDescription + " but found multiple doubles " + matchingDoubles);
		}
	}

	private List<T> matchingDoubles(Predicate<T> matcher) {
		return doubles.stream().filter(matcher).collect(toList());
	}

	private int matchingDoublesCount(Predicate<T> matcher) {
		return matchingDoubles(matcher).size();
	}

	private Predicate<T> filterByName(String beaName) {
		return doubleDefinition -> doubleDefinition.hasNameOrAlias(beaName);
	}

	private Predicate<T> filterByClass(Class<?> beanClass) {
		return doubleDefinition -> doubleDefinition.hasClass(beanClass);
	}
}
