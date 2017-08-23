package com.pchudzik.springmock.infrastructure.definition.registry;

import com.pchudzik.springmock.infrastructure.definition.DoubleDefinition;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DoubleClassResolverTest {
	private static final Class<?> ANY_FIELD_TYPE = Field.class;

	private DoubleClassResolver classResolver = new DoubleClassResolver();

	@Test
	public void should_resolve_double_class_from_declared_annotation_class() {
		final AnnotationDetails detailsWithClass = annotationDetails(DoubleDefinition.class);

		assertEquals(
				DoubleDefinition.class,
				classResolver.resolveDoubleClass(detailsWithClass, ANY_FIELD_TYPE));
	}

	@Test
	public void should_resolve_double_class_from_from_declared_field() {
		final Class<?> fieldType = DoubleDefinition.class;
		final AnnotationDetails detailsWithoutClass = annotationDetails();

		assertEquals(
				fieldType,
				classResolver.resolveDoubleClass(detailsWithoutClass, fieldType));
	}

	@Test
	public void should_throw_exception_when_declared_class_missing_and_field_is_missing() {
		final AnnotationDetails detailsWithoutClass = annotationDetails();
		final Class<?> NO_FIELD_TYPE = null;

		try {
			classResolver.resolveDoubleClass(detailsWithoutClass, NO_FIELD_TYPE);
			fail("Should fail");
		} catch (IllegalArgumentException ex) {
			assertEquals(
					"Can not resolve double class",
					ex.getMessage());
		}
	}

	private AnnotationDetails annotationDetails() {
		return AnnotationDetails.mock(new AutowiredMockBuilder().build());
	}

	private AnnotationDetails annotationDetails(Class<?> doubleClass) {
		return AnnotationDetails.mock(new AutowiredMockBuilder().doubleClass(doubleClass).build());
	}
}