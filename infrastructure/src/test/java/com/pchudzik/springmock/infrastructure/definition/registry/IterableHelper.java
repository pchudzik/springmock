package com.pchudzik.springmock.infrastructure.definition.registry;

class IterableHelper {
	public static <T> T getFirstElement(Iterable<T> iterable) {
		if (!iterable.iterator().hasNext()) {
			throw new IllegalStateException("Expected at leas one element");
		}

		return iterable.iterator().next();
	}
}
