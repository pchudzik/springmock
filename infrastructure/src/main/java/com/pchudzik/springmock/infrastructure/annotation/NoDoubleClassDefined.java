package com.pchudzik.springmock.infrastructure.annotation;

public final class NoDoubleClassDefined {
	public static boolean isDoubleClassDefinitionMissing(Class<?> aClass) {
		return aClass == null || NoDoubleClassDefined.class.equals(aClass);
	}
}
