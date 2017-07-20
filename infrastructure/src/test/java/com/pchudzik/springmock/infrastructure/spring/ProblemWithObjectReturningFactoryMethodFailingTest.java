package com.pchudzik.springmock.infrastructure.spring;

import org.junit.Assert;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@org.junit.runner.RunWith(org.springframework.test.context.junit4.SpringRunner.class)
public class ProblemWithObjectReturningFactoryMethodFailingTest {
	@Autowired
	Service service;

	@Autowired
	DependableService dependableService;

	//org.junit.Test
	public void should_create_context() {
		Assert.assertNotNull(service);
		Assert.assertNotNull(dependableService);
	}

	@Configuration
	static class Config {
		@Bean
		BeanFactoryPostProcessor postProcessor() {
			return new PostProcessor();
		}

		@Bean
		ServiceFactory service2FactoryMethod() {
			return new ServiceFactory();
		}

		@Bean
		DependableService dependableService(@Autowired Service service) {
			return new DependableService(service);
		}
	}

	private static class PostProcessor implements BeanFactoryPostProcessor {
		@Override
		public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
			final BeanDefinitionRegistry definitionRegistry = (BeanDefinitionRegistry) beanFactory;

			definitionRegistry.registerBeanDefinition("service", BeanDefinitionBuilder
					.rootBeanDefinition(Service.class)
					.setFactoryMethodOnBean("createService", "service2FactoryMethod")
					.getBeanDefinition());
		}
	}

	private static class Service {
	}

	private static class ServiceFactory {

		/**
		 * <p>here is the problem
		 * <p>
		 * <p>When method definition is with Object as return type test will fail
		 * <p>
		 * <p>When method definition is with Service as return type test will pass
		 */
		public Object createService() {
			return new Service();
		}
	}

	static class DependableService {
		private final Service service;

		DependableService(Service service) {
			this.service = service;
		}
	}
}
