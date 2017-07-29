package com.pchudzik.springmock.infrastructure.spring.util;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

public class ApplicationContextWalker {
	private final ApplicationContext applicationContext;

	public ApplicationContextWalker(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public BeanDefinition getBeanDefinition(String beanName) {
		return walkContext(ctx -> tryToGetBeanDefinition((BeanDefinitionRegistry) ctx, beanName))
				.stream()
				.flatMap(selectOnlyPresentOptionals())
				.findFirst()
				.orElseThrow(() -> new NoSuchBeanDefinitionException("No bean definition for " + beanName));
	}

	public Collection<String> getBeanDefinitionNames() {
		return walkContext(ctx -> asList(ctx.getBeanDefinitionNames()))
				.stream()
				.flatMap(Collection::stream)
				.collect(Collectors.toSet());
	}

	private <T> Collection<T> walkContext(Function<ApplicationContext, T> contextProcessor) {
		final List<T> result = new LinkedList<>();
		ApplicationContext currentContext = applicationContext;
		while (currentContext != null) {
			T processingResult = contextProcessor.apply(currentContext);
			currentContext = currentContext.getParent();

			result.add(processingResult);
		}

		return result;
	}

	private <T> Function<Optional<T>, Stream<? extends T>> selectOnlyPresentOptionals() {
		return maybeDefinition -> maybeDefinition.map(Stream::of).orElse(Stream.empty());
	}

	private Optional<BeanDefinition> tryToGetBeanDefinition(BeanDefinitionRegistry registry, String beanName) {
		try {
			return Optional.of(registry.getBeanDefinition(beanName));
		} catch (NoSuchBeanDefinitionException ex) {
			return Optional.empty();
		}
	}
}
