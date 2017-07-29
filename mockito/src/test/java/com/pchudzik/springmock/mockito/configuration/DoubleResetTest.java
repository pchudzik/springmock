package com.pchudzik.springmock.mockito.configuration;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import com.pchudzik.springmock.infrastructure.definition.DoubleDefinition;
import com.pchudzik.springmock.infrastructure.definition.registry.DoubleSearch;
import org.junit.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.pchudzik.springmock.mockito.configuration.ConfigurationHelper.findDoublesInClass;
import static com.pchudzik.springmock.mockito.configuration.ConfigurationHelper.getConfiguration;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DoubleResetTest {
	@Test
	public void should_parse_mock_reset_mode() {
		//when
		final DoubleSearch doubles = findDoublesInClass(AnyTest.class);
		final DoubleDefinition neverResetDefinition = doubles.findOneDefinition(AnyTest.neverResetField);
		final DoubleDefinition beforeTestResetDefinition = doubles.findOneDefinition(AnyTest.beforeTestField);
		final DoubleDefinition afterTestResetDefinition = doubles.findOneDefinition(AnyTest.afterTestField);

		//then
		assertFalse(getConfiguration(neverResetDefinition).shouldResetAfterTest());
		assertFalse(getConfiguration(neverResetDefinition).shouldResetBeforeTest());

		assertTrue(getConfiguration(beforeTestResetDefinition).shouldResetBeforeTest());
		assertFalse(getConfiguration(beforeTestResetDefinition).shouldResetAfterTest());

		assertTrue(getConfiguration(afterTestResetDefinition).shouldResetAfterTest());
		assertFalse(getConfiguration(afterTestResetDefinition).shouldResetBeforeTest());
	}

	@Test
	public void should_parse_mock_reset_mode_from_meta_annotation() {
		//when
		final DoubleSearch doubles = findDoublesInClass(AnyTest.class);
		final DoubleDefinition definition = doubles.findOneDefinition(AnyTest.metaAnnotationField);

		//then
		assertTrue(getConfiguration(definition).shouldResetBeforeTest());
		assertFalse(getConfiguration(definition).shouldResetAfterTest());
	}

	private static class AnyTest {
		public static final String neverResetField = "neverReset";
		public static final String beforeTestField = "beforeTestReset";
		public static final String afterTestField = "afterTestReset";
		public static final String metaAnnotationField = "metaAnnotation";

		@AutowiredSpy
		@MockitoDouble(resetMode = MockitoDouble.DoubleResetMode.NEVER)
		private Object neverReset;

		@AutowiredSpy
		@MockitoDouble(resetMode = MockitoDouble.DoubleResetMode.BEFORE_TEST)
		private Object beforeTestReset;

		@AutowiredMock
		@MockitoDouble(resetMode = MockitoDouble.DoubleResetMode.AFTER_TEST)
		private Object afterTestReset;

		@ResetMockBeforeTest
		private Object metaAnnotation;
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@AutowiredMock
	@MockitoDouble(resetMode = MockitoDouble.DoubleResetMode.BEFORE_TEST)
	private @interface ResetMockBeforeTest {
	}
}