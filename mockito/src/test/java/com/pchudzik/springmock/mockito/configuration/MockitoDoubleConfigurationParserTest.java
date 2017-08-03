package com.pchudzik.springmock.mockito.configuration;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import com.pchudzik.springmock.mockito.configuration.matchers.HasDefaultAnswer;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.MockSettings;
import org.mockito.Mockito;
import org.mockito.internal.stubbing.answers.CallsRealMethods;
import org.mockito.internal.stubbing.answers.DoesNothing;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Field;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.never;

public class MockitoDoubleConfigurationParserTest {
	private static final String ANY_NAME = "any double name";
	private MockSettings mockSettings = ConfigurationHelper.mockSettingsMock();
	final MockitoDoubleConfigurationParser configurationParser = new MockitoDoubleConfigurationParser();

	@Test
	public void should_set_default_answer_for_spy_beans() {
		//given
		final MockitoDoubleConfiguration configuration = configurationParser.parseSpyConfiguration(ANY_NAME, AnyTest.spy());

		//when
		configuration.createMockSettings(mockSettings);

		//then
		Mockito
				.verify(mockSettings)
				.defaultAnswer(argThat(HasDefaultAnswer.hasAnswerOfType(CallsRealMethods.class)));
	}

	@Test
	public void should_override_default_answer_for_spy_beans() {
		//given
		final MockitoDoubleConfiguration configuration = configurationParser.parseSpyConfiguration(ANY_NAME, AnyTest.withAnswer());

		//when
		configuration.createMockSettings(mockSettings);

		//then
		final InOrder defaultAnswer = Mockito.inOrder(mockSettings);
		defaultAnswer
				.verify(mockSettings)
				.defaultAnswer(argThat(HasDefaultAnswer.hasAnswerOfType(CallsRealMethods.class)));

		defaultAnswer
				.verify(mockSettings)
				.defaultAnswer(argThat(HasDefaultAnswer.hasAnswerOfType(DoesNothing.class)));
	}

	@Test
	public void should_leave_default_answer_unmodified_for_mock_when_not_configured() {
		//given
		final MockitoDoubleConfiguration configuration = configurationParser.parseMockConfiguration(ANY_NAME, AnyTest.mock());

		//when
		configuration.createMockSettings(mockSettings);

		//then
		Mockito
				.verify(mockSettings, never())
				.defaultAnswer(any());
	}

	@Test
	public void should_apply_mock_name_based_on_provided_argument() {
		//given
		final String mockName = ANY_NAME;
		final MockitoDoubleConfiguration configuration = configurationParser.parseMockConfiguration(mockName, AnyTest.mock());

		//when
		configuration.createMockSettings(mockSettings);

		//then
		Mockito
				.verify(mockSettings)
				.name(mockName);
	}

	@Test
	public void should_generate_spy_name_from_provided_argument() {
		//given
		final String spyName = ANY_NAME;
		final MockitoDoubleConfiguration configuration = configurationParser.parseSpyConfiguration(spyName, AnyTest.spy());

		//when
		configuration.createMockSettings(mockSettings);

		//then
		Mockito
				.verify(mockSettings)
				.name(spyName);
	}

	private static class AnyTest {
		@AutowiredSpy
		Object spy;

		@AutowiredMock
		Object mock;

		@AutowiredSpy
		@MockitoDouble(defaultAnswer = DoesNothing.class)
		Object withAnswer;

		@AutowiredSpy(name = "my spy")
		Object namedSpy;

		@AutowiredMock(name = "my mock")
		Object namedMock;

		public static MockitoDouble namedSpy() {
			return getField("namedSpy");
		}

		public static MockitoDouble namedMock() {
			return getField("namedMock");
		}

		public static MockitoDouble spy() {
			return getField("spy");
		}

		public static MockitoDouble withAnswer() {
			return getField("withAnswer");
		}

		public static MockitoDouble mock() {
			return getField("mock");
		}

		private static MockitoDouble getField(String fieldName) {
			final Field field = FieldUtils.getField(AnyTest.class, fieldName, true);
			return AnnotationUtils.getAnnotation(field, MockitoDouble.class);
		}
	}
}