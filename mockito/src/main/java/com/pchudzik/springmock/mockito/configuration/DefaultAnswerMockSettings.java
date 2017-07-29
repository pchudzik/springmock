package com.pchudzik.springmock.mockito.configuration;

import org.mockito.MockSettings;
import org.mockito.stubbing.Answer;
import org.springframework.beans.BeanUtils;

import java.util.Objects;

class DefaultAnswerMockSettings implements SettingsDecorator {
	private final Class<? extends Answer> answerClass;

	public DefaultAnswerMockSettings(Class<? extends Answer> answerClass) {
		this.answerClass = answerClass;
	}

	@Override
	public MockSettings apply(MockSettings mockSettings) {
		if(Answer.class.equals(answerClass)) {
			return mockSettings;
		}

		return mockSettings.defaultAnswer(createAnswerInstance());
	}

	private Answer createAnswerInstance() {
		return BeanUtils.instantiateClass(answerClass);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final DefaultAnswerMockSettings that = (DefaultAnswerMockSettings) o;
		return Objects.equals(answerClass, that.answerClass);
	}

	@Override
	public int hashCode() {
		return Objects.hash(answerClass);
	}
}
