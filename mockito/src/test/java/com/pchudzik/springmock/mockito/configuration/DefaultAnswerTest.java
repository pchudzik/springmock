package com.pchudzik.springmock.mockito.configuration;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import com.pchudzik.springmock.infrastructure.definition.registry.DoubleSearch;
import org.junit.Test;
import org.mockito.MockSettings;
import org.mockito.Mockito;
import org.mockito.internal.stubbing.answers.ReturnsArgumentAt;
import org.mockito.internal.stubbing.defaultanswers.ReturnsMoreEmptyValues;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.pchudzik.springmock.mockito.configuration.ConfigurationHelper.*;
import static com.pchudzik.springmock.mockito.configuration.matchers.HasDefaultAnswer.hasAnswerOfType;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;

public class DefaultAnswerTest {
	private MockSettings mockSettings = mockSettingsMock();

	@Test
	public void should_not_set_answer_when_field_not_annotated() {
		//given
		final DoubleSearch doubles = findDoublesInClass(AnyTest.class);

		//when
		getConfiguration(doubles.findOneDefinition(AnyTest.withoutDefaultAnswerField)).createMockSettings(mockSettings);

		//then
		Mockito
				.verify(mockSettings, Mockito.never())
				.defaultAnswer(any());
	}

	@Test
	public void should_create_answer_object_instance_for_settings() {
		//given
		final DoubleSearch doubles = findDoublesInClass(AnyTest.class);

		//when
		getConfiguration(doubles.findOneDefinition(AnyTest.returnsSecondArgumentField)).createMockSettings(mockSettings);

		//then
		Mockito
				.verify(mockSettings)
				.defaultAnswer(argThat(hasAnswerOfType(ReturnsSecondArgument.class)));
	}

	@Test
	public void should_create_answer_for_meta_annotated_field() {
		//given
		final DoubleSearch doubles = findDoublesInClass(AnyTest.class);

		//when
		getConfiguration(doubles.findOneDefinition(AnyTest.metaAnnotatedField)).createMockSettings(mockSettings);

		//then
		Mockito
				.verify(mockSettings)
				.defaultAnswer(argThat(hasAnswerOfType(ReturnsSecondArgument.class)));
	}

	private static class AnyTest {
		public static final String returnsSecondArgumentField = "returnsSecondArgument";
		public static final String withoutDefaultAnswerField = "withoutDefaultAnswer";
		public static final String metaAnnotatedField = "complexMock";

		@AutowiredMock
		@MockitoDouble(defaultAnswer = ReturnsMoreEmptyValues.class)
		Object withEmptyValuesMock;

		@AutowiredSpy
		@MockitoDouble(defaultAnswer =  ReturnsSecondArgument.class)
		Object returnsSecondArgument;

		@AutowiredMock
		Object withoutDefaultAnswer;

		@ComplexMock
		Object complexMock;
	}

	private static class ReturnsSecondArgument extends ReturnsArgumentAt {
		private ReturnsSecondArgument() {
			super(1);
		}
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@AutowiredMock
	@MockitoDouble(defaultAnswer = ReturnsSecondArgument.class)
	@interface ComplexMock {
	}
}
