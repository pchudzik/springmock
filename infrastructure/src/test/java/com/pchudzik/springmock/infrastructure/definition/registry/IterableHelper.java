package com.pchudzik.springmock.infrastructure.definition.registry;

import java.util.Iterator;

class IterableHelper {
	public static <T> T getOnlyElement(Iterable<T> iterable) {
		final Iterator<T> iterator = iterable.iterator();

		if (!iterator.hasNext()) {
			throw new IllegalStateException("Expected at leas one element");
		}

		final T result = iterator.next();

		if(iterator.hasNext()) {
			throw new IllegalStateException("Expected exactly one element but was more " + iterable);
		}

		return result;
	}
}
