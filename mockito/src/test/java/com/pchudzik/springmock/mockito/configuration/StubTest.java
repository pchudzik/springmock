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

public class StubTest {
	private MockSettings mockSettings = mockSettingsMock();

	@Test
	public void should_not_set_stub_only_for_not_annotated_element() {
		//given
		final DoubleSearch doubles = findDoublesInClass(AnyTest.class);

		//when
		getConfiguration(doubles.findOneDefinition(AnyTest.notStubField)).createMockSettings(mockSettings);

		//then
		Mockito
				.verify(mockSettings, never())
				.stubOnly();
	}

	@Test
	public void should_set_serializable_mode_in_moc_settings() {
		//given
		final DoubleSearch doubles = findDoublesInClass(AnyTest.class);

		//when
		getConfiguration(doubles.findOneDefinition(AnyTest.stubOnlyField)).createMockSettings(mockSettings);

		//then
		Mockito
				.verify(mockSettings)
				.stubOnly();
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
				.stubOnly();
	}

	private static class AnyTest {
		public static final String notStubField = "notStub";
		public static final String stubOnlyField = "stubOnly";
		public static final String metaAnnotatedField = "complexMock";

		@AutowiredMock
		Object notStub;

		@AutowiredSpy
		@MockitoDouble(stub = true)
		Object stubOnly;

		@ComplexMock
		Object complexMock;
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@AutowiredMock
	@MockitoDouble(stub = true)
	@interface ComplexMock {}
}
