package com.pchudzik.springmock.spock.configuration;

import com.pchudzik.springmock.spock.configuration.SpockDouble.ConstructorArgumentsProvider;
import org.spockframework.mock.IDefaultResponse;
import org.spockframework.mock.MockImplementation;
import org.spockframework.mock.MockNature;
import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.Map;

public class SpockDoubleConfiguration {
	private final Map<String, Object> settings;

	private SpockDoubleConfiguration(Map<String, Object> settings) {
		this.settings = new HashMap<>(settings);
	}

	public static SpockDoubleConfigurationBuilder builder() {
		return new SpockDoubleConfigurationBuilder();
	}

	public Map<String, Object> createDoubleConfiguration() {
		return new HashMap<>(settings);
	}

	public static class SpockDoubleConfigurationBuilder {

		private final Map<String, Object> settings = new HashMap<>();

		private SpockDoubleConfigurationBuilder() {
		}

		public SpockDoubleConfigurationBuilder name(String doubleName) {
			settings.put(SpockSettingsKeys.NAME, doubleName);
			return this;
		}

		public SpockDoubleConfigurationBuilder defaultResponse(Class<? extends IDefaultResponse> defaultResponse) {
			if (IDefaultResponse.class.equals(defaultResponse)) {
				return this;
			}

			settings.put(SpockSettingsKeys.DEFAULT_RESPONSE, createObjectInstance(defaultResponse));
			return this;
		}

		public SpockDoubleConfigurationBuilder nature(MockNature mockNature) {
			settings.put(SpockSettingsKeys.NATURE, mockNature);
			return this;
		}

		public SpockDoubleConfigurationBuilder implementation(MockImplementation implementation) {
			settings.put(SpockSettingsKeys.IMPLEMENTATION, implementation);
			return this;
		}

		public SpockDoubleConfigurationBuilder global(boolean isGlobal) {
			settings.put(SpockSettingsKeys.GLOBAL, isGlobal);
			return this;
		}

		public SpockDoubleConfigurationBuilder stub(boolean isStub) {
			settings.put(SpockSettingsKeys.VERIFIED, !isStub);
			return this;
		}

		public SpockDoubleConfigurationBuilder useObjenesis(boolean doUse) {
			settings.put(SpockSettingsKeys.USE_OBJENESIS, doUse);
			return this;
		}

		public SpockDoubleConfigurationBuilder constructorArgs(Class<? extends ConstructorArgumentsProvider> constructorArgumentsProviderClass) {
			if(ConstructorArgumentsProvider.class.equals(constructorArgumentsProviderClass)) {
				return this;
			}

			final ConstructorArgumentsProvider argumentsProvider = createObjectInstance(constructorArgumentsProviderClass);
			settings.put(SpockSettingsKeys.CONSTRUCTOR_ARGS, argumentsProvider.getConstructorArguments());
			return this;
		}

		public SpockDoubleConfiguration build() {
			return new SpockDoubleConfiguration(settings);
		}

		private <T> T createObjectInstance(Class<T> objectClass) {
			return BeanUtils.instantiateClass(objectClass);
		}
	}
}
