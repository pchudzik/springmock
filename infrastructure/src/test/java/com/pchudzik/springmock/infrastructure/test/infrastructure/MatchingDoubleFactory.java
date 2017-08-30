package com.pchudzik.springmock.infrastructure.test.infrastructure;

import com.pchudzik.springmock.infrastructure.DoubleFactory;
import com.pchudzik.springmock.infrastructure.definition.DoubleDefinition;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class MatchingDoubleFactory implements DoubleFactory {
	private final Map<MockMatcher, Supplier<Object>> mocksMatcher;
	private final Map<SpyMatcher, Supplier<Object>> spiesMatcher;

	private MatchingDoubleFactory(Map<MockMatcher, Supplier<Object>> mocksMatcher, Map<SpyMatcher, Supplier<Object>> spiesMatcher) {
		this.mocksMatcher = mocksMatcher;
		this.spiesMatcher = spiesMatcher;
	}

	public static MatchingDoubleFactoryBuilder builder() {
		return new MatchingDoubleFactoryBuilder();
	}

	@Override
	public Object createMock(DoubleDefinition mockDefinition) {
		return findDouble(
				mocksMatcher.entrySet().stream().filter(entry -> entry.getKey().test(mockDefinition)).map(Entry::getValue),
				mockDefinition);
	}

	@Override
	public Object createSpy(@Nullable Object bean, DoubleDefinition spyDefinition) {
		return findDouble(
				spiesMatcher.entrySet().stream().filter(entry -> entry.getKey().test(bean, spyDefinition)).map(Entry::getValue),
				spyDefinition);
	}

	private Object findDouble(Stream<Supplier<Object>> filteredDoubles, DoubleDefinition doubleDefinition) {
		return filteredDoubles
				.findFirst()
				.map(Supplier::get)
				.orElseThrow(() -> new NoSuchElementException("missing double definition " + doubleDefinition));
	}

	public static class MatchingDoubleFactoryBuilder {
		private final Map<MockMatcher, Supplier<Object>> mocks = new LinkedHashMap<>();
		private final Map<SpyMatcher, Supplier<Object>> spies = new LinkedHashMap<>();

		private MatchingDoubleFactoryBuilder() {
		}

		public static MockMatcher byNameMockMatcher(String name) {
			return def -> def.hasNameOrAlias(name);
		}

		public static SpyMatcher byNameSpyMatcher(String name) {
			return (object, def) -> byNameMockMatcher(name).test(def);
		}

		public MatchingDoubleFactoryBuilder mock(Supplier<Object> mock, MockMatcher matcher) {
			this.mocks.put(matcher, mock);
			return this;
		}

		public MatchingDoubleFactoryBuilder mock(Object mock, MockMatcher matcher) {
			return mock(() -> mock, matcher);
		}

		public MatchingDoubleFactoryBuilder spy(Supplier<Object> spySupplier, SpyMatcher spyMatcher) {
			this.spies.put(spyMatcher, spySupplier);
			return this;
		}

		public MatchingDoubleFactoryBuilder spy(Object spy, SpyMatcher spyMatcher) {
			return spy(() -> spy, spyMatcher);
		}

		public MatchingDoubleFactory build() {
			return new MatchingDoubleFactory(mocks, spies);
		}
	}

	public interface MockMatcher extends Predicate<DoubleDefinition> {
	}

	public interface SpyMatcher extends BiPredicate<Object, DoubleDefinition> {
	}

}
