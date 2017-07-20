package com.pchudzik.springmock.infrastructure.spring;

import com.pchudzik.springmock.infrastructure.DoubleFactory;
import com.pchudzik.springmock.infrastructure.definition.SpyDefinition;
import com.pchudzik.springmock.infrastructure.definition.registry.DoubleRegistry;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class SpyReplacingContextPostProcessorTest {
	private DoubleFactory doubleFactory = Mockito.mock(DoubleFactory.class);

	@Test
	public void should_process_nothing_when_spy_not_registered() {
		//given
		final Object anyBean = new Object();
		final String anyBeanName = "any bean";
		final SpyReplacingContextPostProcessor postProcessor = new SpyProcessorBuilder().build();

		//when
		final Object processedBean = postProcessor.postProcessAfterInitialization(anyBean, anyBeanName);

		//then
		assertSame(anyBean, processedBean);
		Mockito
				.verify(doubleFactory, never())
				.createSpy(any(), any());
	}

	@Test
	public void should_match_by_name_AND_class_when_multiple_beans_of_required_type_present_in_context() {
		//given
		final Service service1 = new Service();
		final Service service2 = new Service();
		final String serviceName = "service1";
		final SpyDefinition spyDefinition = new SpyDefinition(Service.class, serviceName);

		final SpyReplacingContextPostProcessor processor = new SpyProcessorBuilder()
				.withBean(serviceName, service1)
				.withSpy(spyDefinition)

				.withBean("any service", service2)
				.withSpy(new SpyDefinition(Service.class, "some yet another spy"))
				.build();

		//when
		processor.postProcessAfterInitialization(service1, serviceName);

		//then
		Mockito
				.verify(doubleFactory)
				.createSpy(service1, spyDefinition);
		verifyNoMoreInteractions(doubleFactory);
	}

	@Test
	public void should_match_by_class_when_single_bean_of_required_type_present_int_context() {
		//given
		final Service service = new Service();
		final String serviceName = "service";
		final String spyName = "spy";
		final SpyDefinition spyDefinition = new SpyDefinition(Service.class, spyName);
		final SpyReplacingContextPostProcessor postProcessor = new SpyProcessorBuilder()
				.withBean(serviceName, service)
				.withSpy(spyDefinition)
				.build();

		//when
		postProcessor.postProcessAfterInitialization(service, serviceName);

		//then
		Mockito
				.verify(doubleFactory)
				.createSpy(service, spyDefinition);
		verifyNoMoreInteractions(doubleFactory);
	}

	@Test
	public void should_match_nothing_when_multiple_spies_registered_and_has_no_exact_spy_match() {
		//given
		final String serviceName = "service";
		final Service service = new Service();
		final String childServiceName = "childService";
		final ChildService childService = new ChildService();
		final SpyReplacingContextPostProcessor postProcessor = new SpyProcessorBuilder()
				.withBean(serviceName, service)
				.withBean(childServiceName, childService)
				.withSpy(new SpyDefinition(Service.class, "spy"))
				.withSpy(new SpyDefinition(Service.class, "otherSpy"))
				.build();

		//when
		postProcessor.postProcessAfterInitialization(service, serviceName);
		postProcessor.postProcessAfterInitialization(childService, childServiceName);

		//then
		verifyZeroInteractions(doubleFactory);
	}

	@Test
	public void should_match_spy_definition_by_name_even_if_class_is_no_match() {
		//given
		final String serviceName = "service";
		final OtherService otherService = new OtherService();
		final SpyDefinition spyDefinition = new SpyDefinition(Service.class, serviceName);
		final SpyReplacingContextPostProcessor postProcessor = new SpyProcessorBuilder()
				.withBean(serviceName, otherService)
				.withSpy(spyDefinition)
				.build();

		//when
		postProcessor.postProcessAfterInitialization(otherService, serviceName);

		//then
		Mockito
				.verify(doubleFactory)
				.createSpy(otherService, spyDefinition);
	}

	private class SpyProcessorBuilder {
		private List<TestBeanDefinition> beans = new LinkedList<>();
		private List<SpyDefinition> spies = new LinkedList<>();

		public SpyProcessorBuilder withBean(String name, Object bean) {
			beans.add(new TestBeanDefinition(name, bean));
			return this;
		}

		public SpyProcessorBuilder withSpy(SpyDefinition spyDefinition) {
			spies.add(spyDefinition);
			return this;
		}

		public SpyReplacingContextPostProcessor build() {
			return new SpyReplacingContextPostProcessor(
					createContext(beans),
					new DoubleRegistry(
							emptyList(),
							spies),
					doubleFactory);
		}

		private ApplicationContext createContext(Collection<TestBeanDefinition> beans) {
			final DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
			final GenericApplicationContext applicationContext = new GenericApplicationContext(beanFactory);

			beans.forEach(b -> b.addToBeanFactory(beanFactory));
			applicationContext.refresh();

			return applicationContext;
		}

		private class TestBeanDefinition {
			private final String name;
			private final Object bean;

			private TestBeanDefinition(String name, Object bean) {
				this.name = name;
				this.bean = bean;
			}

			public void addToBeanFactory(DefaultListableBeanFactory beanFactory) {
				beanFactory.registerSingleton(name, bean);
			}
		}
	}

	private static class Service {
	}

	private static class ChildService extends Service {
	}

	private static class OtherService {
	}
}