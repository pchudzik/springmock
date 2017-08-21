package com.pchudzik.springmock.infrastructure.test;

import com.pchudzik.springmock.infrastructure.DoubleFactory;
import com.pchudzik.springmock.infrastructure.definition.DoubleDefinition;

import javax.annotation.Nullable;

class FixedDoubleFactory implements DoubleFactory {
	public static final Object MOCK = new Object();
	public static final Object SPY = new Object();

	@Override
	public Object createMock(DoubleDefinition mockDefinition) {
		return MOCK;
	}

	@Override
	public Object createSpy(@Nullable Object bean, DoubleDefinition spyDefinition) {
		return SPY;
	}
}
