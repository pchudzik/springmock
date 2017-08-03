package com.pchudzik.springmock.infrastructure.spring.cache;

import com.pchudzik.springmock.infrastructure.ParseNothingConfigurationParser;
import com.pchudzik.springmock.infrastructure.definition.DoubleDefinition;
import com.pchudzik.springmock.infrastructure.definition.registry.DoubleRegistry;
import com.pchudzik.springmock.infrastructure.spring.MockContextCustomizer;
import com.pchudzik.springmock.infrastructure.spring.MockContextCustomizerFactory;
import com.pchudzik.springmock.infrastructure.DoubleFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.*;
import org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate;
import org.springframework.test.context.cache.DefaultContextCache;
import org.springframework.test.context.support.DefaultTestContextBootstrapper;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.springframework.test.util.ReflectionTestUtils.getField;

/**
 * <p>Ugly hacks to access spring context cache and then count contexts for particular test classes.</p>
 *
 * <p>The sole purpose of this class is to bootstrap spring test context and easily access static context cache and
 * inject springmock infrastructure to ensure that contexts enriched by springmocks are cacheable.</p>
 */
class CachedContextBootstrapper extends DefaultTestContextBootstrapper {
	private static DefaultCacheAwareContextLoaderDelegate cacheDelegate;

	@Override
	protected CacheAwareContextLoaderDelegate getCacheAwareContextLoaderDelegate() {
		final CacheAwareContextLoaderDelegate loaderDelegate = super.getCacheAwareContextLoaderDelegate();
		CachedContextBootstrapper.cacheDelegate = (DefaultCacheAwareContextLoaderDelegate) loaderDelegate;
		return loaderDelegate;
	}

	@Override
	protected List<ContextCustomizerFactory> getContextCustomizerFactories() {
		final List<ContextCustomizerFactory> customizers = super.getContextCustomizerFactories();
		//push fake context customizer which will initialize springmock infrastructure for tests processing
		customizers.add(new TestContextCustomizer());
		return customizers;
	}

	private static class TestContextCustomizer extends MockContextCustomizerFactory {
		public TestContextCustomizer() {
			super(Annotation.class, new ParseNothingConfigurationParser());
		}

		@Override
		protected ContextCustomizer createContextCustomizer(DoubleRegistry doubleRegistry) {
			return new MockContextCustomizer(FakeMockFactory.class, doubleRegistry, Collections.emptyMap());
		}
	}

	private static class FakeMockFactory implements DoubleFactory {
		@Override
		public Object createMock(DoubleDefinition mockDefinition) {
			return new Object();
		}

		@Override
		public Object createSpy(Object bean, DoubleDefinition spyDefinition) {
			return bean;
		}
	}

	static int getCachedContextsSize(String testClassNamePrefix) {
		try {
			final DefaultContextCache contextCache = (DefaultContextCache) getField(cacheDelegate, "contextCache");
			final Map<MergedContextConfiguration, ApplicationContext> contextMap = (Map<MergedContextConfiguration, ApplicationContext>) getField(contextCache, "contextMap");
			return (int) contextMap
					.keySet()
					.stream()
					.filter(config -> config.getTestClass().getSimpleName().startsWith(testClassNamePrefix))
					.count();
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}
}
