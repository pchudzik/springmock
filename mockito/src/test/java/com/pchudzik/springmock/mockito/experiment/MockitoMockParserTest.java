package com.pchudzik.springmock.mockito.experiment;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import org.mockito.internal.stubbing.answers.CallsRealMethods;
import org.mockito.stubbing.Answer;
import org.springframework.core.annotation.AnnotationUtils;

import java.io.Closeable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

class MockitoMockParserTest {
	public static void main(String[] args) {
		final MockitoDouble annotation = AnnotationUtils.findAnnotation(AnyConfiguration.class, MockitoDouble.class);
		final Map<String, Object> attributes = AnnotationUtils.getAnnotationAttributes(annotation, false, true);
		final AutowiredMock autowiredMock = AnnotationUtils.findAnnotation(AnyConfiguration.class, AutowiredMock.class);
		System.out.println(annotation);
	}

	@AutowiredMock
	@MockitoDouble(doubleInterfaces = {Closeable.class})
	private Object mock;

	@WithMock(
			value = Object.class,
			configuration = @MockitoDouble(doubleInterfaces = Closeable.class))
	@WithSpy(
			value = Object.class,
			configuration = @MockitoDouble(
					defaultAnswer = CallsRealMethods.class,
					doubleInterfaces = Closeable.class
			))
	private static class AnyConfiguration {
	}

	@interface WithMock {
		Class<?> value();

		MockitoDouble[] configuration() default {};
	}

	/*
	each mocking library implementation will have to provide similar annotation and provide means to parse and recognize it
	 */
	@interface WithSpy {
		Class<?> value();

		MockitoDouble [] configuration() default {};
	}

	@Target({ElementType.TYPE, ElementType.FIELD})
	@Retention(RetentionPolicy.RUNTIME)
	@interface MockitoDouble {
		Class<? extends Answer> defaultAnswer() default Answer.class;

		Class<?>[] doubleInterfaces() default {};
	}

	@interface SpockDouble {
		String mockNature();
		String defaultResponse();
	}

}