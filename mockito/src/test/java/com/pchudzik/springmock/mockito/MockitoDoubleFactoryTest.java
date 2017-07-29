package com.pchudzik.springmock.mockito;

import com.pchudzik.springmock.infrastructure.DoubleFactory;
import com.pchudzik.springmock.infrastructure.definition.DoubleDefinition;
import com.pchudzik.springmock.mockito.configuration.MockitoDoubleConfiguration;
import com.pchudzik.springmock.mockito.configuration.matchers.PresentInAnyOrder;
import org.junit.Test;
import org.mockito.MockSettings;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.listeners.InvocationListener;
import org.mockito.listeners.MethodInvocationReport;
import org.mockito.mock.SerializableMode;
import org.mockito.stubbing.Answer;
import org.springframework.aop.framework.AopInfrastructureBean;

import java.io.Closeable;

import static com.pchudzik.springmock.mockito.configuration.matchers.HasDefaultAnswer.hasAnswerOfType;
import static com.pchudzik.springmock.mockito.configuration.matchers.HasInvocationListener.hasInstancesOf;
import static org.mockito.Matchers.argThat;

public class MockitoDoubleFactoryTest {
	private MockSettings mockSettings = Mockito.spy(Mockito.withSettings());
	private DoubleFactory doubleFactory = new MockitoDoubleFactory(() -> mockSettings);

	@Test
	public void should_create_mock_with_configuration() {
		//given
		final DoubleDefinition doubleDefinition = DoubleDefinition.builder()
				.name("mock")
				.doubleClass(Object.class)
				.doubleConfiguration(MockitoDoubleConfiguration.builder()
						.stubOnly(true)
						.verboseLogging(true)
						.serializableMode(SerializableMode.ACROSS_CLASSLOADERS)
						.answer(NoAnswer.class)
						.extraInterfaces(new Class[]{Closeable.class})
						.invocationListeners(new Class[]{DoNothingInvocationListener.class})
						.build())
				.build();

		//when
		doubleFactory.createMock(doubleDefinition);

		//then
		Mockito
				.verify(mockSettings)
				.stubOnly();
		Mockito
				.verify(mockSettings)
				.verboseLogging();
		Mockito
				.verify(mockSettings)
				.serializable(SerializableMode.ACROSS_CLASSLOADERS);
		Mockito
				.verify(mockSettings)
				.defaultAnswer(argThat(hasAnswerOfType(NoAnswer.class)));
		Mockito
				.verify(mockSettings)
				.extraInterfaces(argThat(new PresentInAnyOrder(AopInfrastructureBean.class, Closeable.class)));
		Mockito
				.verify(mockSettings)
				.invocationListeners(argThat(hasInstancesOf(DoNothingInvocationListener.class)));
	}

	public static class NoAnswer implements Answer<Object> {
		public NoAnswer() {
		}

		@Override
		public Object answer(InvocationOnMock invocation) throws Throwable {
			return null;
		}
	}

	public static class DoNothingInvocationListener implements InvocationListener {
		public DoNothingInvocationListener() {}

		@Override
		public void reportInvocation(MethodInvocationReport methodInvocationReport) {
		}
	}
}
