package com.pchudzik.springmock.mockito.configuration.matchers;

import org.hamcrest.Matcher;
import org.mockito.ArgumentMatcher;
import org.mockito.stubbing.Answer;

import java.util.Objects;

public class HasDefaultAnswer extends ArgumentMatcher {
	private final Class<? extends Answer> expectedAnswerClass;

	public static Matcher<? extends Answer> hasAnswerOfType(Class<? extends Answer> expectedAnswerClass) {
		return new HasDefaultAnswer(expectedAnswerClass);
	}

	private HasDefaultAnswer(Class<? extends Answer> expectedAnswerClass) {
		this.expectedAnswerClass = expectedAnswerClass;
	}

	@Override
	public boolean matches(Object argument) {
		final Answer actual = (Answer) argument;
		return Objects.equals(
				actual.getClass(),
				expectedAnswerClass);
	}
}
