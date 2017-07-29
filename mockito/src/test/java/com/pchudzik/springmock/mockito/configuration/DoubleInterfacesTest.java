package com.pchudzik.springmock.mockito.configuration;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import com.pchudzik.springmock.infrastructure.definition.registry.DoubleSearch;
import com.pchudzik.springmock.mockito.configuration.matchers.PresentInAnyOrder;
import org.junit.Test;
import org.mockito.MockSettings;
import org.mockito.Mockito;
import org.springframework.beans.factory.FactoryBean;

import java.io.Closeable;
import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.pchudzik.springmock.mockito.configuration.MockitoDoubleConfiguration.MockitoDoubleConfigurationBuilder.AOP_INFRASTRUCTURE_BEAN;
import static com.pchudzik.springmock.mockito.configuration.ConfigurationHelper.findDoublesInClass;
import static com.pchudzik.springmock.mockito.configuration.ConfigurationHelper.getConfiguration;
import static com.pchudzik.springmock.mockito.configuration.ConfigurationHelper.mockSettingsMock;
import static org.mockito.Matchers.argThat;

public class DoubleInterfacesTest {
	private MockSettings mockSettings = mockSettingsMock();

	@Test
	public void should_assign_additional_interfaced_to_mock_settings() {
		//given
		final DoubleSearch doubles = findDoublesInClass(AnyTest.class);

		//when
		getConfiguration(doubles.findOneDefinition(AnyTest.withExtraInterfacesField))
				.createMockSettings(mockSettings);

		//then
		Mockito
				.verify(mockSettings)
				.extraInterfaces(argThat(new PresentInAnyOrder(AOP_INFRASTRUCTURE_BEAN, Closeable.class, FactoryBean.class)));
	}

	@Test
	public void should_not_assign_only_aop_infrastructure_interface_when_no_interfaces_provided() {
		//given
		final DoubleSearch doubles = findDoublesInClass(AnyTest.class);

		//when
		getConfiguration(doubles.findOneDefinition(AnyTest.withoutExtraInterfacesField))
				.createMockSettings(mockSettings);

		//then
		Mockito
				.verify(mockSettings)
				.extraInterfaces(AOP_INFRASTRUCTURE_BEAN);
	}

	@Test
	public void should_assign_extra_interfaces_from_meta_annotation() {
		//given
		final DoubleSearch doubles = findDoublesInClass(AnyTest.class);

		//when
		getConfiguration(doubles.findOneDefinition(AnyTest.metaField))
				.createMockSettings(mockSettings);

		//then
		Mockito
				.verify(mockSettings)
				.extraInterfaces(argThat(new PresentInAnyOrder(AOP_INFRASTRUCTURE_BEAN, Serializable.class, Closeable.class)));
	}

	private static class AnyTest {
		public static final String withExtraInterfacesField = "spyWithExtraInterfaces";
		public static final String withoutExtraInterfacesField = "mockWithoutExtraInterfaces";
		public static final String metaField = "complexMock";

		@AutowiredSpy
		@MockitoDouble(extraInterfaces = {Closeable.class, FactoryBean.class})
		Object spyWithExtraInterfaces;

		@AutowiredMock
		Object mockWithoutExtraInterfaces;

		@ComplexMock
		Object complexMock;
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@AutowiredMock
	@MockitoDouble(extraInterfaces = {Serializable.class, Closeable.class})
	private @interface ComplexMock {
	}

}
