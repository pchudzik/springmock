package com.pchudzik.springmock.infrastructure.spring;

import com.pchudzik.springmock.infrastructure.DoubleFactory;
import com.pchudzik.springmock.infrastructure.MockConstants;
import com.pchudzik.springmock.infrastructure.definition.DoubleDefinition;
import com.pchudzik.springmock.infrastructure.definition.registry.DoubleRegistry;
import com.pchudzik.springmock.infrastructure.definition.registry.DoubleSearch;
import com.pchudzik.springmock.infrastructure.spring.util.ApplicationContextWalker;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.context.ApplicationContext;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Post processor which detects if created object instance should be spied on. If true then replace
 * newly created bean with spy created by {@link DoubleFactory}.
 */
public class ToSpyReplacingProcessor extends InstantiationAwareBeanPostProcessorAdapter {
	public static final String BEAN_NAME = MockConstants.PACKAGE_PREFIX + "spyRegistrationContextPostProcessor";
	private static final Logger log = Logger.getLogger(ToSpyReplacingProcessor.class.getName());

	private final DoubleRegistry doubleRegistry;
	private final DoubleFactory doubleFactory;
	private final DoubleDefinitionsRegistrationContext doubleDefinitionsRegistrationContext;
	private final ApplicationContext applicationContext;

	public ToSpyReplacingProcessor(ApplicationContext applicationContext, DoubleRegistry doubleRegistry, DoubleFactory doubleFactory, DoubleDefinitionsRegistrationContext doubleDefinitionsRegistrationContext) {
		this.doubleRegistry = doubleRegistry;
		this.doubleFactory = doubleFactory;
		this.doubleDefinitionsRegistrationContext = doubleDefinitionsRegistrationContext;
		this.applicationContext = applicationContext;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		final Class<?> beanClass = resolveBeanClass(bean);
		final DoubleSearch spySearch = doubleRegistry.spySearch();

		if(spySearch.containsAnyDoubleMatching(beanName, beanClass)) {
			if(spySearch.containsExactlyOneDouble(beanName, beanClass)) {
				final DoubleDefinition spyDefinition = spySearch.findOneDefinition(beanName, beanClass);
				return createSpy(bean, spyDefinition);
			} else if(spySearch.containsExactlyOneDouble(beanName)) {
				final DoubleDefinition spyDefinition = spySearch.findOneDefinition(beanName);
				return createSpy(bean, spyDefinition);
			} else if(spySearch.containsExactlyOneDouble(beanClass)) {
				final DoubleDefinition spyDefinition = spySearch.findOneDefinition(beanClass);
				if(hasOnlyOneBeanOfClass(spyDefinition.getDoubleClass())) {
					return createSpy(bean, spyDefinition);
				}
			} else {
				log.log(Level.WARNING, "Found more than one spy matching " + beanClass.getCanonicalName() + " and " + beanName);
			}
		}

		return bean;
	}

	private Object createSpy(Object bean, DoubleDefinition spyDefinition) {
		if(doubleDefinitionsRegistrationContext.isBeanDefinitionRegisteredForDouble(spyDefinition)) {
			return bean;
		}

		//in case when spy instance exists then I need to register spy definition manually otherwise registrationContext will not be aware of this bean
		doubleDefinitionsRegistrationContext.registerSpyReplacement(spyDefinition);
		return doubleFactory.createSpy(unwrapAopProxy(bean), spyDefinition);
	}

	private boolean hasOnlyOneBeanOfClass(Class<?> beanClass) {
		return new ApplicationContextWalker(applicationContext).hasOnlyOneBeanOfClass(beanClass);
	}

	/**
	 * Beans which are processed by this processor might be already wrapped in some kind of the
	 * proxy (transactional or async) therefore to properly initialze spy I need to unwrap it
	 *
	 * @param bean
	 * @return
	 */
	private Class<?> resolveBeanClass(Object bean) {
		return unwrapAopProxy(bean).getClass();
	}

	private static Object unwrapAopProxy(Object bean) {
		if (AopUtils.isAopProxy(bean) && bean instanceof Advised) {
			try {
				return ((Advised) bean).getTargetSource().getTarget();
			} catch (Exception e) {
				throw new IllegalStateException("Can not unwrap aop proxy", e);
			}
		}

		return bean;
	}
}
