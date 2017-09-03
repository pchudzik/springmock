package com.pchudzik.springmock.infrastructure.definition.registry;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.Iterator;

public class DoubleSearchMatchers {
	public static Matcher<DoubleSearch> hasSize(final int expectedSize) {
		return new BaseMatcher<DoubleSearch>() {
			@Override
			public boolean matches(Object item) {
				return sizeOf(item) == expectedSize;
			}

			@Override
			public void describeTo(Description description) {
				description
						.appendText("size ").appendValue(expectedSize);
			}

			@Override
			public void describeMismatch(Object item, Description description) {
				description
						.appendText("was  ")
						.appendValue(sizeOf(item))
						.appendText(" in ")
						.appendValue(item);
			}

			private int sizeOf(Object item) {
				final Iterable<DoubleSearch> doubles = (Iterable<DoubleSearch>) item;
				int size = 0;

				for (Iterator<DoubleSearch> it = doubles.iterator(); it.hasNext(); it.next()) {
					size += 1;
				}

				return size;
			}
		};
	}
}
