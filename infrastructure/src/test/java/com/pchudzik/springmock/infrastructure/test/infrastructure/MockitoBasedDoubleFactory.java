package com.pchudzik.springmock.infrastructure.test.infrastructure;

import com.pchudzik.springmock.infrastructure.DoubleFactory;
import com.pchudzik.springmock.infrastructure.definition.DoubleDefinition;
import org.mockito.Mockito;

import javax.annotation.Nullable;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;

public class MockitoBasedDoubleFactory implements DoubleFactory {
	private final DoubleFactory doubleFactory;
	private final Object mock;
	private final Object spy;

	private MockitoBasedDoubleFactory(Object mock, Object spy) {
		this.doubleFactory = Mockito.mock(DoubleFactory.class);
		this.mock =mock;
		this.spy = spy;

		Mockito.when(doubleFactory.createMock(any(DoubleDefinition.class))).thenReturn(mock);
		Mockito.when(doubleFactory.createSpy(anyObject(), any(DoubleDefinition.class))).thenReturn(spy);
	}

	public static MockitoBasedDoubleFactoryBuilder builder() {
		return new MockitoBasedDoubleFactoryBuilder();
	}

	public static MockitoBasedDoubleFactory instance() {
		return MockitoBasedDoubleFactory.builder().build();
	}

	@Override
	public Object createMock(DoubleDefinition mockDefinition) {
		return doubleFactory.createMock(mockDefinition);
	}

	@Override
	public Object createSpy(@Nullable Object bean, DoubleDefinition spyDefinition) {
		return doubleFactory.createSpy(bean, spyDefinition);
	}


	public DoubleFactory getMockedDoubleFactory() {
		return doubleFactory;
	}

	public Object getMock() {
		return mock;
	}

	public Object getSpy() {
		return spy;
	}

	public static class MockitoBasedDoubleFactoryBuilder {
		private Object mock = new Object();
		private Object spy = new Object();

		public MockitoBasedDoubleFactoryBuilder mock(Object o) {
			this.mock = o;
			return this;
		}

		public MockitoBasedDoubleFactoryBuilder spy(Object o) {
			this.spy = o;
			return this;
		}

		public MockitoBasedDoubleFactory build() {
			return new MockitoBasedDoubleFactory(mock, spy);
		}
	}
}
