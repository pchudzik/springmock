package com.pchudzik.springmock.infrastructure.test.infrastructure;

import com.pchudzik.springmock.infrastructure.DoubleConfigurationParser;
import com.pchudzik.springmock.infrastructure.DoubleFactoryCreator;
import com.pchudzik.springmock.infrastructure.ParseNothingConfigurationParser;
import com.pchudzik.springmock.infrastructure.definition.registry.DoubleRegistry;
import com.pchudzik.springmock.infrastructure.spring.MockContextCustomizer;
import com.pchudzik.springmock.infrastructure.spring.MockContextCustomizerFactory;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;
import org.springframework.test.context.support.DefaultTestContextBootstrapper;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public abstract class SpringMockContextBootstrapper extends DefaultTestContextBootstrapper {
	private final DoubleFactoryCreator doubleFactoryCreator;
	private final Supplier<DoubleConfigurationParser> doubleConfigurationParserSupplier;

	public SpringMockContextBootstrapper(DoubleFactoryCreator doubleFactoryCreator) {
		this(doubleFactoryCreator, ParseNothingConfigurationParser::new);
	}

	public SpringMockContextBootstrapper(DoubleFactoryCreator doubleFactoryCreator, Supplier<DoubleConfigurationParser> doubleConfigurationParserSupplier) {
		this.doubleFactoryCreator = doubleFactoryCreator;
		this.doubleConfigurationParserSupplier = doubleConfigurationParserSupplier;
	}

	@Override
	protected List<ContextCustomizerFactory> getContextCustomizerFactories() {
		final List<ContextCustomizerFactory> customizers = new ArrayList<>(super.getContextCustomizerFactories());
		customizers.add(new TestContextCustomizer());
		return customizers;
	}

	private class TestContextCustomizer extends MockContextCustomizerFactory {
		TestContextCustomizer() {
			super(Annotation.class, doubleConfigurationParserSupplier.get());
		}

		@Override
		protected ContextCustomizer createContextCustomizer(DoubleRegistry doubleRegistry) {
			return new MockContextCustomizer(
					doubleFactoryCreator,
					doubleRegistry,
					Collections.emptyMap());
		}
	}
}
