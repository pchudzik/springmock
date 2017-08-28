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

public class MatchingDoubleFactory implements DoubleFactory {
	private final Map<MockMatcher, Object> mocksMatcher;
	private final Map<SpyMatcher, Object> spiesMatcher;

	private MatchingDoubleFactory(Map<MockMatcher, Object> mocksMatcher, Map<SpyMatcher, Object> spiesMatcher) {
		this.mocksMatcher = mocksMatcher;
		this.spiesMatcher = spiesMatcher;
	}

	public static MatchingDoubleFactoryBuilder builder() {
		return new MatchingDoubleFactoryBuilder();
	}

	@Override
	public Object createMock(DoubleDefinition mockDefinition) {
		return mocksMatcher.entrySet().stream()
				.filter(entry -> entry.getKey().test(mockDefinition))
				.findFirst()
				.map(Entry::getValue)
				.orElseThrow(() -> new NoSuchElementException("No mock matching " + mockDefinition));
	}

	@Override
	public Object createSpy(@Nullable Object bean, DoubleDefinition spyDefinition) {
		return spiesMatcher.entrySet().stream()
				.filter(entry -> entry.getKey().test(bean, spyDefinition))
				.findFirst()
				.map(Entry::getValue)
				.orElseThrow(() -> new NoSuchElementException("No spy definition for " + bean + " with definition " + spyDefinition));
	}

	public static class MatchingDoubleFactoryBuilder {
		private final Map<MockMatcher, Object> mocks = new LinkedHashMap<>();
		private final Map<SpyMatcher, Object> spies = new LinkedHashMap<>();

		private MatchingDoubleFactoryBuilder() {
		}

		public static MockMatcher byNameMockMatcher(String name) {
			return def -> name.equals(def.getName());
		}

		public static SpyMatcher byNameSpyMatcher(String name) {
			return (object, def) -> byNameMockMatcher(name).test(def);
		}

		public MatchingDoubleFactoryBuilder mock(Object mock, MockMatcher matcher) {
			mocks.put(matcher, mock);
			return this;
		}

		public MatchingDoubleFactoryBuilder spy(Object spy, SpyMatcher spyMatcher) {
			spies.put(spyMatcher, spy);
			return this;
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
