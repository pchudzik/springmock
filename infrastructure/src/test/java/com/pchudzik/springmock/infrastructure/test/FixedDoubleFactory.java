package com.pchudzik.springmock.infrastructure.test;

import com.pchudzik.springmock.infrastructure.DoubleFactory;
import com.pchudzik.springmock.infrastructure.definition.DoubleDefinition;

import javax.annotation.Nullable;

class FixedDoubleFactory implements DoubleFactory {
	private final Object mock;
	private final Object spy;

	private FixedDoubleFactory(Object mock, Object spy) {
		this.mock = mock;
		this.spy = spy;
	}

	public static FixedDoubleFactoryBuilder builder() {
		return new FixedDoubleFactoryBuilder();
	}

	public static FixedDoubleFactory instance() {
		return builder().build();
	}

	@Override
	public Object createMock(DoubleDefinition mockDefinition) {
		return mock;
	}

	@Override
	public Object createSpy(@Nullable Object bean, DoubleDefinition spyDefinition) {
		return spy;
	}

	public Object getMock() {
		return mock;
	}

	public Object getSpy() {
		return spy;
	}

	public static class FixedDoubleFactoryBuilder {
		private Object mock = new Object();
		private Object spy = new Object();

		public FixedDoubleFactoryBuilder mock(Object o) {
			this.mock = o;
			return this;
		}

		public FixedDoubleFactoryBuilder spy(Object o) {
			this.spy = o;
			return this;
		}

		public FixedDoubleFactory build() {
			return new FixedDoubleFactory(mock, spy);
		}
	}
}
