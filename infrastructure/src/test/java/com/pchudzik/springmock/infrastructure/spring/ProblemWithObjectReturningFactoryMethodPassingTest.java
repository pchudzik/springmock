package com.pchudzik.springmock.infrastructure.spring;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @see MockClassResolver
 * @see ProblemWithObjectReturningFactoryMethodFailingTest
 */
@RunWith(SpringRunner.class)
public class ProblemWithObjectReturningFactoryMethodPassingTest {
	@Autowired
	Service service;

	@Test
	public void should_create_context() {
		Assert.assertNotNull(service);
	}

	@Configuration
	static class Config {
		@Bean
		BeanFactoryPostProcessor postProcessor() {
			return new PostProcessor();
		}

		@Bean
		ServiceFactory serviceFactory() {
			return new ServiceFactory();
		}
	}

	private static class PostProcessor implements BeanFactoryPostProcessor {
		@Override
		public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
			final BeanDefinitionRegistry definitionRegistry = (BeanDefinitionRegistry) beanFactory;

			definitionRegistry.registerBeanDefinition("service", BeanDefinitionBuilder
					.rootBeanDefinition(Service.class)
					.setFactoryMethodOnBean("createService", "serviceFactory")
					.getBeanDefinition());
		}
	}

	private static class Service {
	}

	private static class ServiceFactory {

		/**
		 * In this case when service is not being injected into other service
		 * then spring is able to properly resolve target bean type
		 */
		public Object createService() {
			return new Service();
		}
	}
}
