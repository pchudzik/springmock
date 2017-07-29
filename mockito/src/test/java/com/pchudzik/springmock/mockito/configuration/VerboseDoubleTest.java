package com.pchudzik.springmock.mockito.configuration;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import com.pchudzik.springmock.infrastructure.definition.registry.DoubleSearch;
import org.junit.Test;
import org.mockito.MockSettings;
import org.mockito.Mockito;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.pchudzik.springmock.mockito.configuration.ConfigurationHelper.*;
import static org.mockito.Mockito.never;

public class VerboseDoubleTest {
	private MockSettings mockSettings = mockSettingsMock();

	@Test
	public void should_not_apply_verbose_configuration_for_not_annotated_element() {
		//given
		final DoubleSearch doubles = findDoublesInClass(AnyTest.class);

		//when
		getConfiguration(doubles.findOneDefinition(AnyTest.nonVerboseField)).createMockSettings(mockSettings);

		//then
		Mockito
				.verify(mockSettings, never())
				.verboseLogging();
	}

	@Test
	public void should_set_serializable_mode_in_moc_settings() {
		//given
		final DoubleSearch doubles = findDoublesInClass(AnyTest.class);

		//when
		getConfiguration(doubles.findOneDefinition(AnyTest.verboseField)).createMockSettings(mockSettings);

		//then
		Mockito
				.verify(mockSettings)
				.verboseLogging();
	}

	@Test
	public void should_resolve_serializable_mode_from_meta_annotations() {
		//given
		final DoubleSearch doubles = findDoublesInClass(AnyTest.class);

		//when
		getConfiguration(doubles.findOneDefinition(AnyTest.metaAnnotatedField)).createMockSettings(mockSettings);

		//then
		Mockito
				.verify(mockSettings)
				.verboseLogging();
	}

	private static class AnyTest {
		public static final String nonVerboseField = "nonVerbose";
		public static final String verboseField = "verboseDouble";
		public static final String metaAnnotatedField = "complexMock";

		@AutowiredMock
		Object nonVerbose;

		@AutowiredSpy
		@MockitoDouble(verbose = true)
		Object verboseDouble;

		@ComplexMock
		Object complexMock;
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@AutowiredMock
	@MockitoDouble(verbose = true)
	@interface ComplexMock {}
}
