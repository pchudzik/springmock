package com.pchudzik.springmock.infrastructure.definition.registry;

import com.pchudzik.springmock.infrastructure.DoubleConfigurationParser;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.*;
import java.lang.reflect.Field;

import static com.pchudzik.springmock.infrastructure.definition.registry.DoubleConfigurationResolver.MISSING_CONFIGURATION_ANNOTATION;
import static com.pchudzik.springmock.infrastructure.definition.registry.DoubleConfigurationResolver.NO_FIELD;
import static org.junit.Assert.assertEquals;

public class DoubleConfigurationResolverTest {
	private static final String doubleName = "aDouble";

	private DoubleConfigurationParser configurationParser = Mockito.mock(DoubleConfigurationParser.class);
	private DoubleConfigurationResolver configurationResolver = new DoubleConfigurationResolver(DoubleConfig.class, configurationParser);

	@Test
	public void should_resolve_spy_configuration() {
		final Object config = new Object();
		Mockito
				.when(configurationParser.parseSpyConfiguration(doubleName, configAnnotationInstance()))
				.thenReturn(config);

		assertEquals(
				config,
				resolveSpyConfiguration(doubleField()));
	}

	@Test
	public void should_resolve_mock_configuration() {
		final Object config = new Object();
		Mockito
				.when(configurationParser.parseMockConfiguration(doubleName, configAnnotationInstance()))
				.thenReturn(config);

		assertEquals(
				config,
				resolveMockConfiguration(doubleField()));
	}

	@Test
	public void should_return_mock_configuration_for_not_annotated_field() {
		resolveMockConfiguration(otherField());

		Mockito
				.verify(configurationParser)
				.parseMockConfiguration(doubleName, MISSING_CONFIGURATION_ANNOTATION);
	}

	@Test
	public void should_return_spy_configuration_from_not_annotated_field() {
		resolveSpyConfiguration(otherField());

		Mockito
				.verify(configurationParser)
				.parseSpyConfiguration(doubleName, MISSING_CONFIGURATION_ANNOTATION);
	}

	@Test
	public void should_resolve_class_level_mock_configuration() {
		configurationResolver.resolveMockConfiguration(doubleName, NO_FIELD);

		Mockito
				.verify(configurationParser)
				.parseMockConfiguration(doubleName, MISSING_CONFIGURATION_ANNOTATION);
	}

	@Test
	public void should_resolve_class_level_spy_configuration() {
		configurationResolver.resolveSpyConfiguration(doubleName, NO_FIELD);

		Mockito
				.verify(configurationParser)
				.parseSpyConfiguration(doubleName, MISSING_CONFIGURATION_ANNOTATION);
	}

	private Object resolveMockConfiguration(Field field) {
		return configurationResolver.resolveMockConfiguration(doubleName, field);
	}

	private Object resolveSpyConfiguration(Field field) {
		return configurationResolver.resolveSpyConfiguration(doubleName, field);
	}

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	private @interface DoubleConfig {
	}

	private static class WithConfigurationTestCase {
		@DoubleConfig
		Object mock;

		Object otherField;
	}

	public static Field otherField() {
		return getField("otherField");
	}

	public static Field doubleField() {
		return getField("mock");
	}

	public static DoubleConfig configAnnotationInstance() {
		return AnnotationUtils.getAnnotation(doubleField(), DoubleConfig.class);
	}

	private static Field getField(String fieldName) {
		try {
			return ReflectionUtils.findField(WithConfigurationTestCase.class, fieldName);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}