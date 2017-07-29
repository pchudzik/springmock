package com.pchudzik.springmock.mockito.configuration;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.definition.registry.DoubleSearch;
import org.junit.Test;
import org.mockito.MockSettings;
import org.mockito.Mockito;
import org.mockito.listeners.InvocationListener;
import org.mockito.listeners.MethodInvocationReport;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.pchudzik.springmock.mockito.configuration.ConfigurationHelper.*;
import static com.pchudzik.springmock.mockito.configuration.matchers.HasInvocationListener.hasInstancesOf;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;

public class InvocationListenersTest {
	private MockSettings mockSettings = mockSettingsMock();

	@Test
	public void should_not_assign_any_invocation_listener_when_field_not_annotated() {
		//given
		final DoubleSearch doubles = findDoublesInClass(AnyTest.class);

		//when
		getConfiguration(doubles.findOneDefinition(AnyTest.withoutListenersField)).createMockSettings(mockSettings);

		//then
		Mockito
				.verify(mockSettings, Mockito.never())
				.invocationListeners(any());
	}

	@Test
	public void should_create_invocation_listeners_object_instances_for_requested_listeners() {
		//given
		final DoubleSearch doubles = findDoublesInClass(AnyTest.class);

		//when
		getConfiguration(doubles.findOneDefinition(AnyTest.withListenersField)).createMockSettings(mockSettings);

		//then
		Mockito
				.verify(mockSettings)
				.invocationListeners(argThat(hasInstancesOf(Listener1.class, Listener2.class)));
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
				.invocationListeners(argThat(hasInstancesOf(Listener2.class)));
	}

	private static class AnyTest {
		public static final String withListenersField = "withListeners";
		public static final String withoutListenersField = "withoutListener";
		public static final String metaAnnotatedField = "complexMock";

		@AutowiredMock
		@MockitoDouble(invocationListeners = {Listener1.class, Listener2.class})
		Object withListeners;

		@AutowiredMock
		Object withoutListener;

		@ComplexMock
		Object complexMock;
	}

	private static class Listener1 implements InvocationListener {
		private Listener1() {
		}

		@Override
		public void reportInvocation(MethodInvocationReport methodInvocationReport) {
		}
	}

	private static class Listener2 implements InvocationListener {
		private Listener2() {
		}

		@Override
		public void reportInvocation(MethodInvocationReport methodInvocationReport) {
		}
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@AutowiredMock
	@MockitoDouble(invocationListeners = Listener2.class)
	@interface ComplexMock {
	}
}
