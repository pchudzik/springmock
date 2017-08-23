package com.pchudzik.springmock.infrastructure.annotation;

import org.junit.Test;

import static com.pchudzik.springmock.infrastructure.annotation.NoDoubleClassDefined.isDoubleClassDefinitionMissing;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NoDoubleClassDefinedTest {
	@Test
	public void should_detect_missing_class_definition() {
		assertTrue(isDoubleClassDefinitionMissing(null));
		assertTrue(isDoubleClassDefinitionMissing(NoDoubleClassDefined.class));
	}

	@Test
	public void should_detect_existing_class_definition() {
		assertFalse(isDoubleClassDefinitionMissing(Object.class));
	}
}