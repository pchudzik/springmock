package com.pchudzik.springmock.mockito.configuration;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import com.pchudzik.springmock.infrastructure.definition.registry.DoubleSearch;
import org.junit.Test;
import org.mockito.MockSettings;
import org.mockito.Mockito;
import org.mockito.mock.SerializableMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.pchudzik.springmock.mockito.configuration.ConfigurationHelper.findDoublesInClass;
import static com.pchudzik.springmock.mockito.configuration.ConfigurationHelper.getConfiguration;
import static com.pchudzik.springmock.mockito.configuration.ConfigurationHelper.mockSettingsMock;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;

public class DoubleSerializableModeTest {
	private MockSettings mockSettings = mockSettingsMock();

	@Test
	public void should_not_set_serializable_mode_for_not_annotated_element() {
		//given
		final DoubleSearch doubles = findDoublesInClass(AnyTest.class);

		//when
		getConfiguration(doubles.findOneDefinition(AnyTest.noSerializationModeField)).createMockSettings(mockSettings);

		//then
		Mockito
				.verify(mockSettings, never())
				.serializable(any());
	}

	@Test
	public void should_set_serializable_mode_in_moc_settings() {
		//given
		final DoubleSearch doubles = findDoublesInClass(AnyTest.class);

		//when
		getConfiguration(doubles.findOneDefinition(AnyTest.withSerializationModeField)).createMockSettings(mockSettings);

		//then
		Mockito
				.verify(mockSettings)
				.serializable(SerializableMode.ACROSS_CLASSLOADERS);

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
				.serializable(SerializableMode.NONE);
	}

	private static class AnyTest {
		public static final String noSerializationModeField = "noSerializationMode";
		public static final String withSerializationModeField = "withSerializationMode";
		public static final String metaAnnotatedField = "complexMock";

		@AutowiredMock
		Object noSerializationMode;

		@AutowiredMock
		@MockitoDouble(serializableMode = SerializableMode.ACROSS_CLASSLOADERS)
		Object withSerializationMode;

		@ComplexMock
		Object complexMock;
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@AutowiredSpy
	@MockitoDouble(serializableMode = SerializableMode.NONE)
	private @interface ComplexMock {
	}
}
