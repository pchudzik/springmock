package com.pchudzik.springmock.infrastructure.definition.registry;

import com.pchudzik.springmock.infrastructure.DoubleConfigurationParser;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import com.pchudzik.springmock.infrastructure.definition.DoubleDefinition;
import org.springframework.context.annotation.Configuration;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.pchudzik.springmock.infrastructure.definition.registry.DoubleNameResolver.resolveDoubleName;
import static java.util.Arrays.asList;
import static org.springframework.core.annotation.AnnotationUtils.getAnnotation;
import static org.springframework.core.annotation.AnnotationUtils.getRepeatableAnnotations;
import static org.springframework.util.ReflectionUtils.doWithFields;

/**
 * Parses @{@link AutowiredMock} and @{@link AutowiredSpy} and creates double definitions stored in
 * the registry
 */
public class DoubleDefinitionRegistryFactory {
	private final Class<? extends Annotation> configurationAnnotation;
	private final DoubleConfigurationParser<?, Annotation> configurationParser;

	public DoubleDefinitionRegistryFactory(Class<? extends Annotation> configurationAnnotation, DoubleConfigurationParser configurationParser) {
		this.configurationAnnotation = configurationAnnotation;
		this.configurationParser = configurationParser;
	}

	public DoubleRegistry parse(Class<?> clazz) {
		final Set<DoubleDefinition> mocks = new HashSet<>();
		final Set<DoubleDefinition> spies = new HashSet<>();

		Stream
				.concat(
						findParsableClasses(clazz),
						Stream.of(clazz))
				.forEach(parsableClass -> {
					mocks.addAll(extractDoubleDefinition(parsableClass, this::extractMockDefinition));
					mocks.addAll(extractMocksDefinition(parsableClass));

					spies.addAll(extractDoubleDefinition(parsableClass, this::extractSpyDefinition));
					spies.addAll(extractSpiesDefinition(parsableClass));
				});

		return new DoubleRegistry(mocks, spies);
	}

	protected DoubleDefinition createMockDefinition(Field field, AutowiredMock autowiredMock) {
		final String doubleName = resolveDoubleName(field);
		final DoubleDefinition.DoubleDefinitionBuilder definitionBuilder = DoubleDefinition.builder()
				.doubleClass(field.getType())
				.name(doubleName)
				.aliases(asList(autowiredMock.alias()));

		applyDoubleConfiguration(
				field,
				configuration -> definitionBuilder.doubleConfiguration(configurationParser.parseMockConfiguration(doubleName, configuration)));

		return definitionBuilder.build();
	}

	protected DoubleDefinition createSpyDefinition(Field field, AutowiredSpy autowiredSpy) {
		final String doubleName = resolveDoubleName(field);
		final DoubleDefinition.DoubleDefinitionBuilder definitionBuilder = DoubleDefinition.builder()
				.doubleClass(field.getType())
				.name(doubleName)
				.aliases(asList(autowiredSpy.alias()));

		applyDoubleConfiguration(
				field,
				configuration -> definitionBuilder.doubleConfiguration(configurationParser.parseSpyConfiguration(doubleName, configuration)));

		return definitionBuilder.build();
	}

	private void applyDoubleConfiguration(Field field, Consumer<Annotation> configurationApplier) {
		final Annotation configuration = getAnnotation(field, configurationAnnotation);
		configurationApplier.accept(configuration);
	}

	private Collection<DoubleDefinition> extractDoubleDefinition(Class<?> clazz, Function<Field, Optional<DoubleDefinition>> fieldParser) {
		final Set<DoubleDefinition> spies = new HashSet<>();

		doWithFields(clazz, (Field field) -> {
			fieldParser.apply(field).ifPresent(spies::add);
		});

		return spies;
	}

	private Stream<Class<?>> findParsableClasses(Class<?> clazz) {
		return Stream
				.of(clazz.getDeclaredClasses())
				.filter(declaredClass -> getAnnotation(declaredClass, Configuration.class) != null);
	}

	private Optional<DoubleDefinition> extractMockDefinition(Field field) {
		return Optional
				.ofNullable(getAnnotation(field, AutowiredMock.class))
				.map(autowiredMock -> createMockDefinition(field, autowiredMock));
	}

	private Optional<DoubleDefinition> extractSpyDefinition(Field field) {
		return Optional
				.ofNullable(getAnnotation(field, AutowiredSpy.class))
				.map(autowiredSpy -> createSpyDefinition(field, autowiredSpy));
	}

	private Collection<DoubleDefinition> extractSpiesDefinition(Class<?> clazz) {
		return getRepeatableAnnotations(clazz, AutowiredSpy.class)
				.stream()
				.map(this::createSpy)
				.collect(Collectors.toSet());
	}

	private Collection<DoubleDefinition> extractMocksDefinition(Class<?> clazz) {
		return getRepeatableAnnotations(clazz, AutowiredMock.class)
				.stream()
				.map(this::createMock)
				.collect(Collectors.toSet());
	}

	private DoubleDefinition createMock(AutowiredMock autowiredMock) {
		final Class<?> doubleClass = autowiredMock.doubleClass();
		final String name = resolveDoubleName(autowiredMock);

		checkDoubleClassPresent(doubleClass);

		return DoubleDefinition.builder()
				.aliases(asList(autowiredMock.alias()))
				.name(name)
				.doubleClass(doubleClass)
				.build();
	}

	private DoubleDefinition createSpy(AutowiredSpy autowiredSpy) {
		final Class<?> doubleClass = autowiredSpy.doubleClass();
		final String name = resolveDoubleName(autowiredSpy);

		checkDoubleClassPresent(doubleClass);

		return DoubleDefinition.builder()
				.aliases(asList(autowiredSpy.alias()))
				.name(name)
				.doubleClass(doubleClass)
				.build();
	}

	private void checkDoubleClassPresent(Class<?> doubleClass) {
		if(Void.class.equals(doubleClass)) {
			throw new IllegalArgumentException("Double class is required for class level mocks");
		}
	}
}
