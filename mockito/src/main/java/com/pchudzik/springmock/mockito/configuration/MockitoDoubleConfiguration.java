package com.pchudzik.springmock.mockito.configuration;

import com.pchudzik.springmock.mockito.configuration.MockitoDouble.DoubleResetMode;
import org.mockito.MockSettings;
import org.mockito.listeners.InvocationListener;
import org.mockito.mock.SerializableMode;
import org.mockito.stubbing.Answer;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toSet;

public class MockitoDoubleConfiguration {
	private final DoubleResetMode resetMode;
	private final List<SettingsDecorator> settingsDecorators;

	private MockitoDoubleConfiguration(DoubleResetMode resetMode, Collection<SettingsDecorator> settings) {
		this.resetMode = resetMode;
		this.settingsDecorators = new LinkedList<>(settings);
	}

	public static MockitoDoubleConfigurationBuilder builder() {
		return new MockitoDoubleConfigurationBuilder();
	}

	public static MockitoDoubleConfiguration defaultConfiguration() {
		return builder().build();
	}

	public boolean shouldResetBeforeTest() {
		return resetMode == DoubleResetMode.BEFORE_TEST;
	}

	public boolean shouldResetAfterTest() {
		return resetMode == DoubleResetMode.AFTER_TEST;
	}

	public MockSettings createMockSettings(MockSettings mockSettings) {
		MockSettings settings = mockSettings;
		for (SettingsDecorator decorator : settingsDecorators) {
			settings = decorator.apply(settings);
		}
		return settings;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final MockitoDoubleConfiguration that = (MockitoDoubleConfiguration) o;
		return resetMode == that.resetMode &&
				Objects.equals(settingsDecorators, that.settingsDecorators);
	}

	@Override
	public int hashCode() {
		return Objects.hash(resetMode, settingsDecorators);
	}

	public static class MockitoDoubleConfigurationBuilder {
		final static Class<?> AOP_INFRASTRUCTURE_BEAN = AopInfrastructureBean.class;

		private List<Class<?>> extraInterfaces = emptyList();

		private DoubleResetMode resetMode = MockitoDouble.DEFAULT_RESET_MODE;
		private List<SettingsDecorator> settings = new LinkedList<>();

		public MockitoDoubleConfigurationBuilder resetMode(DoubleResetMode resetMode) {
			this.resetMode = resetMode;
			return this;
		}

		public MockitoDoubleConfigurationBuilder extraInterfaces(Class<?>[] extraInterfaces) {
			this.extraInterfaces = asList(extraInterfaces);
			return this;
		}

		public MockitoDoubleConfigurationBuilder answer(Class<? extends Answer> answerClass) {
			return addSettings(new DefaultAnswerMockSettings(answerClass));
		}

		public MockitoDoubleConfigurationBuilder serializableMode(SerializableMode serializableMode) {
			return addSettings(new DoubleSerializableModeSettings(serializableMode));
		}

		public MockitoDoubleConfigurationBuilder verboseLogging(boolean isVerbose) {
			return addSettings(new VerboseDoubleSettings(isVerbose));
		}

		public MockitoDoubleConfigurationBuilder stubOnly(boolean isStub) {
			return addSettings(new StubSettings(isStub));
		}

		public MockitoDoubleConfigurationBuilder invocationListeners(Class<? extends InvocationListener>[] listeners) {
			return addSettings(new InvocationListenersSettings(listeners));
		}

		private MockitoDoubleConfigurationBuilder addSettings(SettingsDecorator setting) {
			this.settings.add(setting);
			return this;
		}

		public MockitoDoubleConfiguration build() {
			Assert.notNull(resetMode);

			applyConfigurationToPreventProxyCreation();

			return new MockitoDoubleConfiguration(resetMode, settings);
		}

		/**
		 * This will prevent spring from creating proxy for any annotations which causes to wrap bean in proxy (@Async for example)
		 *
		 * @see org.springframework.aop.framework.AbstractAdvisingBeanPostProcessor#postProcessAfterInitialization(java.lang.Object, java.lang.String)
		 */
		private void applyConfigurationToPreventProxyCreation() {
			addSettings(new DoubleInterfacesMockSettings(Stream
					.concat(
							Stream.of(AOP_INFRASTRUCTURE_BEAN),
							extraInterfaces.stream())
					.collect(toSet())));
		}

		public MockitoDoubleConfigurationBuilder name(String name) {
			return addSettings(new DoubleNameSettings(name));
		}
	}
}
