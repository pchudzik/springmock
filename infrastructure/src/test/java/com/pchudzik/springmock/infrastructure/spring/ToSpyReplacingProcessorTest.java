package com.pchudzik.springmock.infrastructure.spring;

import com.pchudzik.springmock.infrastructure.DoubleFactory;
import com.pchudzik.springmock.infrastructure.definition.DoubleDefinition;
import com.pchudzik.springmock.infrastructure.definition.registry.DoubleRegistry;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static com.pchudzik.springmock.infrastructure.definition.DoubleDefinitionTestFactory.doubleDefinition;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(JUnitParamsRunner.class)
public class ToSpyReplacingProcessorTest {
	private DoubleFactory doubleFactory = Mockito.mock(DoubleFactory.class);

	@Test
	public void should_process_nothing_when_spy_not_registered() {
		//given
		final Object anyBean = new Object();
		final String anyBeanName = "any bean";
		final ToSpyReplacingProcessor postProcessor = new TestContextBuilder().build().toSpyReplacingProcessor;

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
		final DoubleDefinition definition = DoubleDefinition.builder()
				.doubleClass(Service.class)
				.name(serviceName)
				.build();

		final ToSpyReplacingProcessor processor = new TestContextBuilder()
				.withBean(serviceName, service1)
				.withSpy(definition)

				.withBean("any service", service2)
				.withSpy(doubleDefinition(Service.class))
				.build()
				.toSpyReplacingProcessor;

		//when
		processor.postProcessAfterInitialization(service1, serviceName);

		//then
		Mockito
				.verify(doubleFactory)
				.createSpy(service1, definition);
		verifyNoMoreInteractions(doubleFactory);
	}

	@Test
	public void should_match_by_class_when_single_bean_of_required_type_present_int_context() {
		//given
		final Service service = new Service();
		final String serviceName = "service";
		final String spyName = "spy";
		final DoubleDefinition definition = doubleDefinition(Service.class, spyName);
		final ToSpyReplacingProcessor postProcessor = new TestContextBuilder()
				.withBean(serviceName, service)
				.withSpy(definition)
				.build()
				.toSpyReplacingProcessor;

		//when
		postProcessor.postProcessAfterInitialization(service, serviceName);

		//then
		Mockito
				.verify(doubleFactory)
				.createSpy(service, definition);
		verifyNoMoreInteractions(doubleFactory);
	}

	@Test
	public void should_match_nothing_when_multiple_spies_registered_and_has_no_exact_spy_match() {
		//given
		final String serviceName = "service";
		final Service service = new Service();
		final String childServiceName = "childService";
		final ChildService childService = new ChildService();
		final ToSpyReplacingProcessor postProcessor = new TestContextBuilder()
				.withBean(serviceName, service)
				.withBean(childServiceName, childService)
				.withSpy(doubleDefinition(Service.class))
				.withSpy(doubleDefinition(Service.class))
				.build()
				.toSpyReplacingProcessor;

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
		final DoubleDefinition spyDefinition = doubleDefinition(Service.class, serviceName);
		final ToSpyReplacingProcessor postProcessor = new TestContextBuilder()
				.withBean(serviceName, otherService)
				.withSpy(spyDefinition)
				.build()
				.toSpyReplacingProcessor;

		//when
		postProcessor.postProcessAfterInitialization(otherService, serviceName);

		//then
		Mockito
				.verify(doubleFactory)
				.createSpy(otherService, spyDefinition);
	}

	@Test
	public void should_not_spy_on_spy() {
		//given
		final String spyName = "myService";
		final Service spy = Mockito.spy(new Service());
		final DoubleDefinition spyDefinition = doubleDefinition(Service.class, spyName);
		final ToSpyReplacingProcessor postProcessor = new TestContextBuilder()
				.withSpyBean(spyName, spy, spyDefinition)
				.withSpy(spyDefinition)
				.build()
				.toSpyReplacingProcessor;

		//when
		postProcessor.postProcessAfterInitialization(spy, spyName);

		//then
		Mockito.verifyZeroInteractions(doubleFactory);
	}

	@Test
	public void should_register_spy_replacement_in_registration_context() {
		//given
		final Service service = new Service();
		final String serviceName = "service";
		final DoubleDefinition definition = DoubleDefinition.builder()
				.doubleClass(Service.class)
				.name(serviceName)
				.build();

		final TestContextBuilder.TestContext testContext = new TestContextBuilder()
				.withBean(serviceName, service)
				.withSpy(definition)
				.build();
		final ToSpyReplacingProcessor processor = testContext.toSpyReplacingProcessor;

		//when
		processor.postProcessAfterInitialization(service, serviceName);

		//then
		assertTrue(testContext
				.doubleDefinitionsRegistrationContext
				.isBeanDefinitionRegisteredForDouble(definition));
	}

	@Test
	@Parameters(method = proxyInterfaceDataProvider)
	@TestCaseName("should_unwrap_{0}_from_spied_on_instance")
	public void should_unwrap_proxy_from_spied_on_instance(String proxyProviderName, boolean proxyTargetClass) throws ClassNotFoundException {
		//given
		final IService rawService = new Service();
		final String serviceName = "service";
		final DoubleDefinition definition = DoubleDefinition.builder()
				.doubleClass(IService.class)
				.name(serviceName)
				.build();
		final TestContextBuilder.TestContext testContext = new TestContextBuilder()
				.withBean(serviceName, createProxyFactoryBean(rawService, proxyTargetClass))
				.withSpy(definition)
				.build();

		//when
		final Object bean = testContext.applicationContext.getBean(serviceName);
		testContext.toSpyReplacingProcessor.postProcessAfterInitialization(bean, serviceName);

		//then
		assertTrue(AopUtils.isAopProxy(bean));
		Mockito.verify(doubleFactory).createSpy(
				argThat(not(isProxy())),
				eq(definition));
	}

	private static final String proxyInterfaceDataProvider = "proxyInterfaceDataProvider";

	@SuppressWarnings("unused")
	private List<Object[]> proxyInterfaceDataProvider() {
		final boolean doProxyTargetClass = true;
		return asList(
				new Object[]{"jdk", !doProxyTargetClass},
				new Object[]{"cglib", doProxyTargetClass}
		);
	}

	private ProxyFactoryBean createProxyFactoryBean(IService rawService, boolean proxyTargetClass) throws ClassNotFoundException {
		final ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
		proxyFactoryBean.setProxyInterfaces(new Class<?>[]{IService.class});
		proxyFactoryBean.setTarget(rawService);
		proxyFactoryBean.setProxyTargetClass(proxyTargetClass);
		return proxyFactoryBean;
	}

	private BaseMatcher<Object> isProxy() {
		return new BaseMatcher<Object>() {
			@Override
			public boolean matches(Object item) {
				return AopUtils.isAopProxy(item);
			}

			@Override
			public void describeTo(Description description) {
			}
		};
	}

	private class TestContextBuilder {
		private final DoubleDefinitionsRegistrationContext doubleDefinitionsRegistrationContext = new DoubleDefinitionsRegistrationContext();
		private List<TestBeanDefinition> beans = new LinkedList<>();
		private List<DoubleDefinition> spies = new LinkedList<>();

		public TestContextBuilder withBean(String name, Object bean) {
			beans.add(new TestBeanDefinition(name, bean));
			return this;
		}

		public TestContextBuilder withSpyBean(String name, Object bean, DoubleDefinition doubleDefinition) {
			beans.add(new TestBeanDefinition(name, bean));
			doubleDefinitionsRegistrationContext.registerSpy(Mockito.mock(BeanDefinitionRegistry.class), doubleDefinition);
			return this;
		}

		public TestContextBuilder withSpy(DoubleDefinition spyDefinition) {
			spies.add(spyDefinition);
			return this;
		}

		public TestContext build() {
			final ApplicationContext applicationContext = createContext(beans);

			return new TestContext(
					applicationContext,
					doubleDefinitionsRegistrationContext,
					new ToSpyReplacingProcessor(
							applicationContext,
							new DoubleRegistry(emptyList(), spies),
							doubleFactory,
							doubleDefinitionsRegistrationContext));
		}

		private ApplicationContext createContext(Collection<TestBeanDefinition> beans) {
			final DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
			final GenericApplicationContext applicationContext = new GenericApplicationContext(beanFactory);

			beans.forEach(b -> b.addToBeanFactory(beanFactory));
			applicationContext.refresh();

			return applicationContext;
		}

		public class TestContext {
			final ApplicationContext applicationContext;
			final DoubleDefinitionsRegistrationContext doubleDefinitionsRegistrationContext;
			final ToSpyReplacingProcessor toSpyReplacingProcessor;

			public TestContext(
					ApplicationContext applicationContext,
					DoubleDefinitionsRegistrationContext doubleDefinitionsRegistrationContext,
					ToSpyReplacingProcessor toSpyReplacingProcessor) {
				this.applicationContext = applicationContext;
				this.doubleDefinitionsRegistrationContext = doubleDefinitionsRegistrationContext;
				this.toSpyReplacingProcessor = toSpyReplacingProcessor;
			}
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

	private interface IService {
	}

	private static class Service implements IService {
	}

	private static class ChildService extends Service {
	}

	private static class OtherService {
	}
}