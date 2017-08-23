package com.pchudzik.springmock.infrastructure.definition.registry;

import com.pchudzik.springmock.infrastructure.definition.DoubleDefinition;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DoubleNameResolverTest {
	private DoubleNameResolver nameResolver = new DoubleNameResolver();

	@Test
	public void should_use_declared_double_name() {
		final String mockName = "mock";
		final AnnotationDetails detailsWithName = annotationDetails(mockName);

		assertEquals(
				mockName,
				nameResolver.resolveDoubleName(detailsWithName));
	}

	@Test
	public void should_use_declared_double_class_to_generate_double_name() {
		final AnnotationDetails detailsWithClass = annotationDetails(DoubleDefinition.class);

		assertEquals(
				nameResolver.resolveDoubleName(detailsWithClass),
				"doubleDefinition");
	}

	@Test
	public void should_use_field_name_to_generate_double_name() {
		final String fieldName = "mockedField";
		final AnnotationDetails emptyDetails = annotationDetails();

		assertEquals(
				fieldName,
				nameResolver.resolveDoubleName(emptyDetails, fieldName));
	}

	@Test
	public void should_fail_when_can_not_generate_name() {
		try {
			nameResolver.resolveDoubleName(annotationDetails());
			fail("should fail");
		} catch (IllegalStateException ex) {
			assertEquals(
					"Can not generate double name",
					ex.getMessage());
		}
	}

	private AnnotationDetails annotationDetails() {
		return AnnotationDetails.mock(new AutowiredMockBuilder().build());
	}

	private AnnotationDetails annotationDetails(Class<?> doubleClass) {
		return AnnotationDetails.mock(new AutowiredMockBuilder().doubleClass(doubleClass).build());
	}

	private AnnotationDetails annotationDetails(String name) {
		return AnnotationDetails.mock(new AutowiredMockBuilder().name(name).build());
	}

	private AnnotationDetails annotationDetails(String name, Class<?> doubleClass) {
		return AnnotationDetails.mock(new AutowiredMockBuilder()
				.name(name)
				.doubleClass(doubleClass)
				.build());
	}
}