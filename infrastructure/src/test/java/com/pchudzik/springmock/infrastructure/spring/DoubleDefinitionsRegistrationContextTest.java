package com.pchudzik.springmock.infrastructure.spring;

import com.pchudzik.springmock.infrastructure.DoubleFactory;
import com.pchudzik.springmock.infrastructure.definition.DoubleDefinition;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.GenericApplicationContext;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DoubleDefinitionsRegistrationContextTest {
	private DoubleFactory doubleFactory = Mockito.mock(DoubleFactory.class);
	private DoubleDefinitionsRegistrationContext registrationContext = new DoubleDefinitionsRegistrationContext();

	@Test
	public void should_register_mock_creation() {
		//final
		final DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		final DoubleDefinition mockDefinition = DoubleDefinition.builder()
				.name("mock")
				.doubleClass(Object.class)
				.build();

		//when
		registrationContext.registerMock(beanFactory, mockDefinition);
		bootstrapApplicationContext(beanFactory);

		//then
		Mockito
				.verify(doubleFactory)
				.createMock(mockDefinition);
	}

	@Test
	public void should_register_spy_creation() {
		//final
		final DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		final DoubleDefinition spyDefinition = DoubleDefinition.builder()
				.name("spy")
				.doubleClass(Object.class)
				.build();

		//when
		registrationContext.registerSpy(beanFactory, spyDefinition);
		bootstrapApplicationContext(beanFactory);

		//then
		Mockito
				.verify(doubleFactory)
				.createSpy(null, spyDefinition);
	}

	@Test
	public void should_detect_already_registered_double() {
		//final
		final DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		final DoubleDefinition otherDouble = DoubleDefinition.builder()
				.name("other double")
				.doubleClass(Object.class)
				.build();
		final DoubleDefinition spyDefinition = DoubleDefinition.builder()
				.name("spy")
				.doubleClass(Object.class)
				.build();
		final DoubleDefinition mockDefinition = DoubleDefinition.builder()
				.name("mock")
				.doubleClass(Object.class)
				.build();
		registrationContext.registerSpy(beanFactory, spyDefinition);
		registrationContext.registerMock(beanFactory, mockDefinition);
		bootstrapApplicationContext(beanFactory);

		//expect
		assertFalse(registrationContext.isBeanDefinitionRegisteredForDouble(otherDouble));
		assertTrue(registrationContext.isBeanDefinitionRegisteredForDouble(spyDefinition));
		assertTrue(registrationContext.isBeanDefinitionRegisteredForDouble(mockDefinition));
	}

	@Test
	public void should_detect_spies_which_replaced_original_beans() {
		//given
		final DoubleDefinition doubleDefinition = DoubleDefinition.builder()
				.name("spy")
				.doubleClass(Object.class)
				.build();

		//when
		registrationContext.registerSpyReplacement(doubleDefinition);

		//then
		assertTrue(registrationContext.isBeanDefinitionRegisteredForDouble(doubleDefinition));
	}

	@Test
	public void should_work_fine_when_bean_overriding_is_disabled() {
		//given
		final String beanName = "someService";
		final DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		beanFactory.setAllowBeanDefinitionOverriding(false);
		beanFactory.registerBeanDefinition(beanName, BeanDefinitionBuilder
				.rootBeanDefinition(Object.class)
				.getBeanDefinition());

		final DoubleDefinition mockDefinition = DoubleDefinition.builder()
				.name(beanName)
				.doubleClass(Object.class)
				.build();

		//when
		registrationContext.registerMock(beanFactory, mockDefinition);
		bootstrapApplicationContext(beanFactory);

		//then
		noExceptionThrown();
		assertTrue(registrationContext.isBeanDefinitionRegisteredForDouble(mockDefinition));
	}

	private void bootstrapApplicationContext(DefaultListableBeanFactory beanFactory) {
		beanFactory.registerSingleton(DoubleFactory.DOUBLE_FACTORY_BEAN_NAME, doubleFactory);
		final GenericApplicationContext applicationContext = new GenericApplicationContext(beanFactory);
		applicationContext.refresh();
	}

	private void noExceptionThrown() {
	}
}
