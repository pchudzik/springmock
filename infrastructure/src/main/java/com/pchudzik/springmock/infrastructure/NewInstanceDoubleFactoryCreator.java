package com.pchudzik.springmock.infrastructure;

import org.springframework.beans.BeanUtils;

import java.util.Objects;

public class NewInstanceDoubleFactoryCreator implements DoubleFactoryCreator {
	private final Class<? extends DoubleFactory> doubleFactoryClass;

	public NewInstanceDoubleFactoryCreator(Class<? extends DoubleFactory> doubleFactoryClass) {
		this.doubleFactoryClass = doubleFactoryClass;
	}

	@Override
	public DoubleFactory createDoubleFactory() {
		return BeanUtils.instantiateClass(doubleFactoryClass);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final NewInstanceDoubleFactoryCreator that = (NewInstanceDoubleFactoryCreator) o;
		return Objects.equals(doubleFactoryClass, that.doubleFactoryClass);
	}

	@Override
	public int hashCode() {
		return Objects.hash(doubleFactoryClass);
	}
}
