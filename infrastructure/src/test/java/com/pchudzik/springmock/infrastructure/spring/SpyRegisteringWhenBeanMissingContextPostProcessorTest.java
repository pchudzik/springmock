package com.pchudzik.springmock.infrastructure.spring;

import com.pchudzik.springmock.infrastructure.DoubleFactory;
import com.pchudzik.springmock.infrastructure.definition.DoubleDefinition;
import com.pchudzik.springmock.infrastructure.definition.registry.DoubleRegistry;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.ApplicationContext;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.stream.Stream;

import static com.pchudzik.springmock.infrastructure.DoubleFactory.DOUBLE_FACTORY_BEAN_NAME;
import static com.pchudzik.springmock.infrastructure.spring.test.ApplicationContextCreator.buildAppContext;
import static java.util.Collections.*;

public class SpyRegisteringWhenBeanMissingContextPostProcessorTest {
	private DoubleFactory doubleFactory = Mockito.mock(DoubleFactory.class);

	@Test
	public void should_create_non_existing_spy_instance_using_double_factory() {
		//given
		final String spyName = "spy";
		final DoubleDefinition spyDefinition = DoubleDefinition.builder()
				.name(spyName)
				.doubleClass(Object.class)
				.build();
		final SpyRegisteringWhenBeanMissingContextPostProcessor postProcessor = createPostProcessor(singleton(spyDefinition));

		//when
		final ApplicationContext context = createApplicationContext(postProcessor, Stream.empty());
		context.getBean(spyName);

		//then
		Mockito
				.verify(doubleFactory)
				.createSpy(null, spyDefinition);
	}

	@Test
	public void should_not_replace_existing_bean_definition_with_spy() {
		//given
		final String beanName = "existingObject";
		final SpyRegisteringWhenBeanMissingContextPostProcessor postProcessor = createPostProcessor(singleton(DoubleDefinition.builder()
				.name(beanName)
				.doubleClass(Object.class)
				.build()));

		//when
		final ApplicationContext applicationContext = createApplicationContext(
				postProcessor,
				Stream.of(new SimpleEntry<>(beanName, new Object())));
		applicationContext.getBean(beanName);

		//then
		Mockito.verifyZeroInteractions(doubleFactory);
	}

	@Test
	public void should_not_replace_existing_bean_definition_when_alias_matches() {
		//given
		final String beanName = "beanName";
		final String spyName = "spy";
		final SpyRegisteringWhenBeanMissingContextPostProcessor postProcessor = createPostProcessor(singleton(DoubleDefinition.builder()
				.name(spyName)
				.aliases(singletonList(beanName))
				.doubleClass(Object.class)
				.build()));

		//when
		final ApplicationContext applicationContext = createApplicationContext(postProcessor, Stream.of(new SimpleEntry<>(beanName, new Object())));
		applicationContext.getBean(beanName);

		//then
		Mockito.verifyZeroInteractions(doubleFactory);
	}

	@Test
	public void should_not_replace_existing_bean_definition_when_bean_defined_in_parent_context() {
		//given
		final String spyName = "rootSpy";
		final SpyRegisteringWhenBeanMissingContextPostProcessor postProcessor = createPostProcessor(singletonList(DoubleDefinition.builder()
				.name(spyName)
				.doubleClass(Object.class)
				.build()));
		final ApplicationContext parentContext = buildAppContext(null, Stream.of(new SimpleEntry<>(spyName, new Object())));
		final ApplicationContext childContext = buildAppContext(parentContext, Stream.of(new SimpleEntry<>(DOUBLE_FACTORY_BEAN_NAME, doubleFactory)), singletonList(postProcessor));

		//when
		childContext.getBean(spyName);

		//then
		Mockito.verifyZeroInteractions(doubleFactory);
	}

	@Test
	public void should_do_nothing_when_no_spies_registered() {
		//given
		final String beanName = "bean";
		final SpyRegisteringWhenBeanMissingContextPostProcessor postProcessor = createPostProcessor(emptyList());

		//when
		final ApplicationContext applicationContext = createApplicationContext(
				postProcessor,
				Stream.of(new SimpleEntry<>(beanName, new Object())));
		applicationContext.getBean(beanName);

		//then
		Mockito.verifyZeroInteractions(doubleFactory);
	}

	private ApplicationContext createApplicationContext(BeanFactoryPostProcessor postProcessor, Stream<Entry<String, Object>> beans) {
		return buildAppContext(
				Stream.concat(
						beans,
						Stream.of(new SimpleEntry<>(DOUBLE_FACTORY_BEAN_NAME, doubleFactory))),
				singletonList(postProcessor));
	}

	private SpyRegisteringWhenBeanMissingContextPostProcessor createPostProcessor(Collection<DoubleDefinition> spies) {
		return new SpyRegisteringWhenBeanMissingContextPostProcessor(new DoubleRegistry(
				emptyList(),
				spies));
	}
}