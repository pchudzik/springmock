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

import java.lang.reflect.Field;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.never;

public class MockitoDoubleConfigurationParserTest {
	private MockSettings mockSettings = ConfigurationHelper.mockSettingsMock();
	final MockitoDoubleConfigurationParser configurationParser = new MockitoDoubleConfigurationParser();

	@Test
	public void should_set_default_answer_for_spy_beans() {
		//given
		final MockitoDoubleConfiguration configuration = configurationParser.parseDoubleConfiguration(AnyTest.spy());

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
		final MockitoDoubleConfiguration configuration = configurationParser.parseDoubleConfiguration(AnyTest.withAnswer());

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
		final MockitoDoubleConfiguration configuration = configurationParser.parseDoubleConfiguration(AnyTest.mock());

		//when
		configuration.createMockSettings(mockSettings);

		//then
		Mockito
				.verify(mockSettings, never())
				.defaultAnswer(any());
	}

	@Test
	public void should_generate_mock_name_based_on_field_name() {
		//given
		final MockitoDoubleConfiguration configuration = configurationParser.parseDoubleConfiguration(AnyTest.mock());

		//when
		configuration.createMockSettings(mockSettings);

		//then
		Mockito
				.verify(mockSettings)
				.name("mock");
	}

	@Test
	public void should_generate_spy_name_based_on_field_name() {
		//given
		final MockitoDoubleConfiguration configuration = configurationParser.parseDoubleConfiguration(AnyTest.spy());

		//when
		configuration.createMockSettings(mockSettings);

		//then
		Mockito
				.verify(mockSettings)
				.name("spy");
	}

	@Test
	public void should_take_mock_name_from_AutowiredMock_name() {
		//given
		final MockitoDoubleConfiguration configuration = configurationParser.parseDoubleConfiguration(AnyTest.namedMock());

		//when
		configuration.createMockSettings(mockSettings);

		//then
		Mockito
				.verify(mockSettings)
				.name("my mock");
	}

	@Test
	public void should_take_spy_name_from_AutowiredSpy_name() {
		//given
		final MockitoDoubleConfiguration configuration = configurationParser.parseDoubleConfiguration(AnyTest.namedSpy());

		//when
		configuration.createMockSettings(mockSettings);

		//then
		Mockito
				.verify(mockSettings)
				.name("my spy");
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

		public static Field namedSpy() {
			return getField("namedSpy");
		}

		public static Field namedMock() {
			return getField("namedMock");
		}

		public static Field spy() {
			return getField("spy");
		}

		public static Field withAnswer() {
			return getField("withAnswer");
		}

		public static Field mock() {
			return getField("mock");
		}

		private static Field getField(String fieldName) {
			return FieldUtils.getField(AnyTest.class, fieldName, true);
		}
	}
}