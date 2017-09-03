package com.pchudzik.springmock.infrastructure.spring.test;

import com.pchudzik.springmock.infrastructure.definition.registry.DoubleRegistry;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toMap;

public class ApplicationContextCreator {
	public static ApplicationContext buildAppContext(Stream<TestBean> beans, Collection<BeanFactoryPostProcessor> postProcessors) {
		return buildAppContext(null, beans, postProcessors);
	}

	public static ApplicationContext buildAppContext(Collection<TestBean> beans) {
		return buildAppContext(beans.stream());
	}

	public static ApplicationContext buildAppContext(Stream<TestBean> beans) {
		return buildAppContext(null, beans);
	}

	public static ApplicationContext buildAppContext(ApplicationContext parent, Stream<TestBean> beans) {
		return buildAppContext(parent, beans, emptyList());
	}

	public static TestBean withEmptyDoubleRegistry() {
		return withDoubleRegistry(emptyDoubleRegistry());
	}

	public static DoubleRegistry emptyDoubleRegistry() {
		return new DoubleRegistry(
				emptyList(),
				emptyList());
	}

	public static TestBean bean(String name, Object bean) {
		return new TestBean(name, bean);
	}

	public static TestBean withDoubleRegistry(DoubleRegistry registry) {
		return bean(DoubleRegistry.BEAN_NAME, registry);
	}

	public static ApplicationContext buildAppContext(ApplicationContext parent, Stream<TestBean> beans, Collection<BeanFactoryPostProcessor> postProcessors) {
		final DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		final GenericApplicationContext applicationContext = new GenericApplicationContext(beanFactory, parent);

		postProcessors.forEach(applicationContext::addBeanFactoryPostProcessor);

		beans.forEach(entry -> {
			final String factoryBean = entry.getName() + "_factory";
			beanFactory.registerSingleton(factoryBean, (Supplier<Object>) entry::getBean);
			beanFactory.registerBeanDefinition(entry.getName(), BeanDefinitionBuilder
					.rootBeanDefinition(entry.getBean() != null ? entry.getBean().getClass() : Object.class)
					.setFactoryMethodOnBean("get", factoryBean)
					.getBeanDefinition());
		});

		applicationContext.refresh();

		return applicationContext;
	}

	private static Map<String, Object> asMap(Stream<TestBean> beans) {
		return beans.collect(toMap(TestBean::getName, TestBean::getBean));
	}

	public static class TestBean {
		final String name;
		final Object bean;

		private TestBean(String name, Object bean) {
			this.name = name;
			this.bean = bean;
		}

		private String getName() {
			return name;
		}

		private Object getBean() {
			return bean;
		}
	}
}
