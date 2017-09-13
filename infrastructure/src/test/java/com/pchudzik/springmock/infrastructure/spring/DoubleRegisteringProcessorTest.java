package com.pchudzik.springmock.infrastructure.spring;

import com.pchudzik.springmock.infrastructure.DoubleFactory;
import com.pchudzik.springmock.infrastructure.definition.DoubleDefinition;
import com.pchudzik.springmock.infrastructure.definition.DoubleDefinitionTestFactory;
import com.pchudzik.springmock.infrastructure.definition.registry.DoubleRegistry;
import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Collections.unmodifiableSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DoubleRegisteringProcessorTest {
	@Test
	public void should_register_mock_definitions_when_destination_bean_does_not_exists() {
		//given
		final DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		final DoubleDefinition mockDefinition = DoubleDefinitionTestFactory.doubleDefinition(Service.class);

		//when
		new DoubleContextBuilder()
				.withMock(mockDefinition)
				.doProcess(beanFactory);

		//then
		assertEquals(
				singleton(mockDefinition.getName()),
				allDefinitionNames(beanFactory));
		assertMockWillBeCreatedByDoubleFactory(beanFactory.getMergedBeanDefinition(mockDefinition.getName()));
	}

	@Test
	public void should_replace_bean_definition_with_mock_definition_when_destination_bean_exists_match_by_name() {
		//given
		final DoubleDefinition mockDefinition = DoubleDefinitionTestFactory.doubleDefinition(Service.class);
		final DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		beanFactory.registerBeanDefinition(mockDefinition.getName(), beanDefinition(Service.class));

		//when
		new DoubleContextBuilder()
				.withMock(mockDefinition)
				.doProcess(beanFactory);

		//then
		assertEquals(
				singleton(mockDefinition.getName()),
				allDefinitionNames(beanFactory));
		assertMockWillBeCreatedByDoubleFactory(beanFactory.getMergedBeanDefinition(mockDefinition.getName()));
	}

	@Test
	public void should_replace_bean_definition_with_mock_definition_when_destination_bean_exists_in_parent_bean_factory_match_by_name() {
		//given
		final DoubleDefinition mockDefinition = DoubleDefinitionTestFactory.doubleDefinition(Service.class);
		final DefaultListableBeanFactory parentFactory = new DefaultListableBeanFactory();
		final DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory(parentFactory);
		parentFactory.registerBeanDefinition(mockDefinition.getName(), beanDefinition(Service.class));

		//when
		new DoubleContextBuilder()
				.withMock(mockDefinition)
				.doProcess(beanFactory);

		//then
		assertEquals(
				singleton(mockDefinition.getName()),
				allDefinitionNames(beanFactory));
		assertMockWillBeCreatedByDoubleFactory(beanFactory.getMergedBeanDefinition(mockDefinition.getName()));
	}

	@Test
	public void should_replace_bean_definition_with_mock_definition_when_single_destination_bean_exists_match_by_class() {
		//given
		final DoubleDefinition mockDefinition = DoubleDefinitionTestFactory.doubleDefinition(Service.class);
		final DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		final String beanDefinitionName = "not " + mockDefinition.getName();
		beanFactory.registerBeanDefinition(beanDefinitionName, beanDefinition(Service.class));

		//when
		new DoubleContextBuilder()
				.withMock(mockDefinition)
				.doProcess(beanFactory);

		//then
		assertEquals(
				singleton(beanDefinitionName),
				allDefinitionNames(beanFactory));
		assertMockWillBeCreatedByDoubleFactory(beanFactory.getMergedBeanDefinition(beanDefinitionName));
	}

	@Test
	public void should_replace_bean_definition_with_mock_definition_when_single_destination_bean_exists_in_parent_bean_factory_match_by_class() {
		//given
		final DoubleDefinition mockDefinition = DoubleDefinitionTestFactory.doubleDefinition(Service.class);
		final String beanDefinitionName = "not" + mockDefinition.getName();
		final DefaultListableBeanFactory parentFactory = new DefaultListableBeanFactory();
		final DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory(parentFactory);
		parentFactory.registerBeanDefinition(beanDefinitionName, beanDefinition(Service.class));

		//when
		new DoubleContextBuilder()
				.withMock(mockDefinition)
				.doProcess(beanFactory);

		//then
		assertEquals(
				singleton(beanDefinitionName),
				allDefinitionNames(beanFactory));
		assertMockWillBeCreatedByDoubleFactory(beanFactory.getMergedBeanDefinition(beanDefinitionName));
	}

	@Test
	public void should_register_mock_as_yet_another_bean_when_multiple_definition_exists() {
		//given
		final DefaultListableBeanFactory parentFactory = new DefaultListableBeanFactory();
		final DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory(parentFactory);
		final DoubleDefinition mockDefinition = DoubleDefinitionTestFactory.doubleDefinition(Service.class);
		final String parentBeanDefinitionName = "parent not " + mockDefinition.getName();
		final String beanDefinitionName = "not " + mockDefinition.getName();
		parentFactory.registerBeanDefinition(parentBeanDefinitionName, beanDefinition(Service.class));
		beanFactory.registerBeanDefinition(beanDefinitionName, beanDefinition(Service.class));

		//when
		new DoubleContextBuilder()
				.withMock(mockDefinition)
				.doProcess(beanFactory);

		//then
		assertEquals(
				asSet(parentBeanDefinitionName, beanDefinitionName, mockDefinition.getName()),
				allDefinitionNames(beanFactory));
		assertMockWillBeCreatedByDoubleFactory(beanFactory.getMergedBeanDefinition(mockDefinition.getName()));
	}

	@Test
	public void should_register_spy_definition_when_destination_bean_does_not_exists() {
		//given
		final DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		final DoubleDefinition spyDefinition = DoubleDefinitionTestFactory.doubleDefinition(Service.class);

		//when
		new DoubleContextBuilder()
				.withSpy(spyDefinition)
				.doProcess(beanFactory);

		//then
		assertEquals(
				singleton(spyDefinition.getName()),
				allDefinitionNames(beanFactory));
		assertSpyIsCreatedByDoubleFactory(beanFactory.getMergedBeanDefinition(spyDefinition.getName()));
	}

	@Test
	public void should_do_nothing_with_existing_bean_when_spy_creation_requested() {
		//given
		final DoubleDefinitionsRegistrationContext registrationContext = new DoubleDefinitionsRegistrationContext();
		final DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		final DoubleDefinition spyDefinition = DoubleDefinitionTestFactory.doubleDefinition(Service.class);
		final String definitionName = "not " + spyDefinition.getName();
		beanFactory.registerBeanDefinition(definitionName, beanDefinition(Service.class));

		//when
		new DoubleContextBuilder()
				.withSpy(spyDefinition)
				.withRegistrationContext(registrationContext)
				.doProcess(beanFactory);
		//then
		assertEquals(
				singleton(definitionName),
				allDefinitionNames(beanFactory));
		assertFalse(registrationContext.isBeanDefinitionRegisteredForDouble(spyDefinition));
	}

	@Test
	public void should_do_nothing_with_existing_bean_in_parent_bean_factory_when_spy_creation_requested() {
		//given
		final DoubleDefinitionsRegistrationContext registrationContext = new DoubleDefinitionsRegistrationContext();
		final DefaultListableBeanFactory parentFactory = new DefaultListableBeanFactory();
		final DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory(parentFactory);
		final DoubleDefinition spyDefinition = DoubleDefinitionTestFactory.doubleDefinition(Service.class);
		final String definitionName = "not " + spyDefinition.getName();
		parentFactory.registerBeanDefinition(definitionName, beanDefinition(Service.class));

		//when
		new DoubleContextBuilder()
				.withSpy(spyDefinition)
				.withRegistrationContext(registrationContext)
				.doProcess(beanFactory);
		//then
		assertEquals(
				singleton(definitionName),
				allDefinitionNames(beanFactory));
		assertFalse(registrationContext.isBeanDefinitionRegisteredForDouble(spyDefinition));
	}

	@Test
	public void should_register_created_mocks_in_registration_context() {
		//given
		final DoubleDefinitionsRegistrationContext registrationContext = new DoubleDefinitionsRegistrationContext();
		final DoubleDefinition mockDefinition = DoubleDefinitionTestFactory.doubleDefinition(Service.class);

		//when
		new DoubleContextBuilder()
				.withRegistrationContext(registrationContext)
				.withMock(mockDefinition)
				.doProcess();

		//then
		assertTrue(registrationContext.isBeanDefinitionRegisteredForDouble(mockDefinition));
	}

	@Test
	public void should_register_created_spies_in_registration_context() {
		//given
		final DoubleDefinitionsRegistrationContext registrationContext = new DoubleDefinitionsRegistrationContext();
		final DoubleDefinition spyDefinition = DoubleDefinitionTestFactory.doubleDefinition(Service.class);

		//when
		new DoubleContextBuilder()
				.withRegistrationContext(registrationContext)
				.withSpy(spyDefinition)
				.doProcess();

		//then
		assertTrue(registrationContext.isBeanDefinitionRegisteredForDouble(spyDefinition));
	}

	private void assertSpyIsCreatedByDoubleFactory(BeanDefinition beanDefinition) {
		assertEquals(DoubleFactory.DOUBLE_FACTORY_BEAN_NAME, beanDefinition.getFactoryBeanName());
		assertEquals(DoubleFactory.CREATE_SPY_FACTORY_METHOD, beanDefinition.getFactoryMethodName());
	}

	private void assertMockWillBeCreatedByDoubleFactory(BeanDefinition beanDefinition) {
		assertEquals(DoubleFactory.DOUBLE_FACTORY_BEAN_NAME, beanDefinition.getFactoryBeanName());
		assertEquals(DoubleFactory.CREATE_MOCK_FACTORY_METHOD, beanDefinition.getFactoryMethodName());
	}

	private AbstractBeanDefinition beanDefinition(Class<Service> beanClass) {
		return BeanDefinitionBuilder.rootBeanDefinition(beanClass).getBeanDefinition();
	}

	private Set<String> allDefinitionNames(DefaultListableBeanFactory beanFactory) {
		final Set<String> result = new HashSet<>();

		DefaultListableBeanFactory factory = beanFactory;
		while (factory != null) {
			final List<String> beanNamesForType = asList(factory.getBeanDefinitionNames());
			result.addAll(beanNamesForType);

			factory = (DefaultListableBeanFactory) factory.getParentBeanFactory();
		}

		return result;
	}

	static class DoubleContextBuilder {
		private final List<DoubleDefinition> mocks = new LinkedList<>();
		private final List<DoubleDefinition> spies = new LinkedList<>();

		private DoubleDefinitionsRegistrationContext registrationContext = new DoubleDefinitionsRegistrationContext();
		private DoubleRegistry doubleRegistry = null;

		public DoubleContextBuilder withMock(DoubleDefinition doubleDefinition) {
			mocks.add(doubleDefinition);
			return this;
		}

		public DoubleContextBuilder withSpy(DoubleDefinition doubleDefinition) {
			spies.add(doubleDefinition);
			return this;
		}

		public DoubleContextBuilder withRegistrationContext(DoubleDefinitionsRegistrationContext registrationContext) {
			this.registrationContext = registrationContext;
			return this;
		}

		public DoubleRegisteringProcessor buildRegisteringProcessor() {
			if (doubleRegistry == null) {
				doubleRegistry = new DoubleRegistry(mocks, spies);
			}

			return new DoubleRegisteringProcessor(
					doubleRegistry,
					registrationContext);
		}

		public void doProcess() {
			doProcess(new DefaultListableBeanFactory());
		}

		public void doProcess(ConfigurableListableBeanFactory beanFactory) {
			buildRegisteringProcessor().postProcessBeanFactory(beanFactory);
		}
	}

	static class Service {
	}

	private static <T> Set<T> asSet(T ... items) {
		final Set<T> result = new HashSet<>(items.length);
		for(T item : items) {
			result.add(item);
		}

		return unmodifiableSet(result);
	}
}