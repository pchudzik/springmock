package com.pchudzik.springmock.infrastructure.definition.registry;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;

interface FromAnnotationDefinitionDetailsExtractor<A extends Annotation> {
	String resolveName();
	String resolveName(Field field);

	Class<?> resolveDoubleClass();
	Class<?> resolveDoubleClass(Field field);

	Collection<String> aliases(A annotation);
}
