package com.pchudzik.springmock.infrastructure.spring;

import com.pchudzik.springmock.infrastructure.DoubleFactory;
import com.pchudzik.springmock.infrastructure.MockConstants;
import com.pchudzik.springmock.infrastructure.definition.SpyDefinition;
import com.pchudzik.springmock.infrastructure.definition.registry.DoubleRegistry;
import com.pchudzik.springmock.infrastructure.definition.registry.DoubleSearch;
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
public class SpyReplacingContextPostProcessor extends InstantiationAwareBeanPostProcessorAdapter {
	public static final String BEAN_NAME = MockConstants.PACKAGE_PREFIX + "spyRegistrationContextPostProcessor";
	private static final Logger log = Logger.getLogger(SpyReplacingContextPostProcessor.class.getName());

	private final DoubleRegistry doubleRegistry;
	private final DoubleFactory doubleFactory;
	private final ApplicationContext applicationContext;

	public SpyReplacingContextPostProcessor(ApplicationContext applicationContext, DoubleRegistry doubleRegistry, DoubleFactory doubleFactory) {
		this.doubleRegistry = doubleRegistry;
		this.doubleFactory = doubleFactory;
		this.applicationContext = applicationContext;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		final Class<?> beanClass = resolveBeanClass(bean);
		final DoubleSearch<SpyDefinition> spySearch = doubleRegistry.spySearch();

		if(spySearch.containsAnyDoubleMatching(beanName, beanClass)) {
			if(spySearch.containsExactlyOneDouble(beanName, beanClass)) {
				final SpyDefinition spyDefinition = spySearch.findOneDefinition(beanName, beanClass);
				return createSpy(bean, spyDefinition);
			} else if(spySearch.containsExactlyOneDouble(beanName)) {
				final SpyDefinition spyDefinition = spySearch.findOneDefinition(beanName);
				return createSpy(bean, spyDefinition);
			} else if(spySearch.containsExactlyOneDouble(beanClass)) {
				final SpyDefinition spyDefinition = spySearch.findOneDefinition(beanClass);
				if(hasOnlyOneBeanOfClass(spyDefinition.getDoubleClass())) {
					return createSpy(bean, spyDefinition);
				}
			} else {
				log.log(Level.WARNING, "Found more than one spy matching " + beanClass.getCanonicalName() + " and " + beanName);
			}
		}

		return bean;
	}

	private Object createSpy(Object bean, SpyDefinition spyDefinition) {
		return doubleFactory.createSpy(unwrapAopProxy(bean), spyDefinition);
	}

	private boolean hasOnlyOneBeanOfClass(Class<?> beanClass) {
		return applicationContext.getBeansOfType(beanClass).size() == 1;
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
