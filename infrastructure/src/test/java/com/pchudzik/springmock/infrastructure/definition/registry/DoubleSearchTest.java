package com.pchudzik.springmock.infrastructure.definition.registry;

import com.pchudzik.springmock.infrastructure.definition.DoubleDefinition;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.pchudzik.springmock.infrastructure.definition.DoubleDefinitionTestFactory.doubleDefinition;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DoubleSearchTest {
	private static final String MISSING_DOUBLE_EXCEPTION_PREFIX = "Expected exactly one double matching";
	private static final String NO_DEFINITION_FOUND_EXCEPTION_MESSAGE = "but found none";
	private static final String MULTIPLE_DOUBLES_EXCEPTION_MESSAGE = "but found multiple doubles";

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void should_find_by_name() {
		//given
		final String mockName = "mock";
		final DoubleDefinition definition = doubleDefinition(Service.class, mockName);

		//when
		final DoubleSearch search = new DoubleSearch(asList(
				doubleDefinition(OtherService.class, "other mock"),
				definition));

		//expect
		assertTrue(search.containsExactlyOneDouble(mockName));
		assertEquals(definition, search.findOneDefinition(mockName));
	}

	@Test
	public void should_match_by_alias() {
		//given
		final String mockAlias = "alias";
		final DoubleDefinition definition = DoubleDefinition.builder()
				.doubleClass(Service.class)
				.name("mockName")
				.aliases(singletonList(mockAlias))
				.build();

		//when
		final DoubleSearch search = new DoubleSearch(singletonList(definition));

		//expect
		assertTrue(search.containsAnyDoubleMatching(mockAlias, Service.class));
		assertTrue(search.containsExactlyOneDouble(mockAlias, Service.class));
		assertTrue(search.containsExactlyOneDouble(mockAlias));

		//and
		assertEquals(
				definition,
				search.findOneDefinition(mockAlias));

		assertEquals(
				definition,
				search.findOneDefinition(mockAlias, Service.class));
	}

	@Test
	public void should_find_by_class() {
		//given
		final Class<Service> mockClass = Service.class;
		final DoubleDefinition definition = doubleDefinition(mockClass);

		//when
		final DoubleSearch search = new DoubleSearch(asList(
				doubleDefinition(OtherService.class),
				definition));

		//expect
		assertTrue(search.containsExactlyOneDouble(mockClass));
		assertEquals(definition, search.findOneDefinition(mockClass));
	}

	@Test
	public void should_find_by_name_and_class() {
		//given
		final String mockName = "mock";
		final Class<Service> mockClass = Service.class;
		final DoubleDefinition definition = doubleDefinition(mockClass, mockName);

		//when
		final DoubleSearch search = new DoubleSearch(asList(
				doubleDefinition(OtherService.class),
				definition));

		//expect
		assertTrue(search.containsExactlyOneDouble(mockName, mockClass));
		assertEquals(definition, search.findOneDefinition(mockName, mockClass));
	}

	@Test
	public void should_match_by_name_or_class() {
		//given
		final String serviceMock = "mock";
		final String otherServiceMock = "other mock";
		final DoubleDefinition serviceDefinition = doubleDefinition(Service.class, serviceMock);
		final DoubleDefinition otherServiceDefinition = doubleDefinition(OtherService.class, otherServiceMock);

		//when
		final DoubleSearch search = new DoubleSearch(asList(
				serviceDefinition,
				otherServiceDefinition));

		//expect
		assertTrue(search.containsAnyDoubleMatching(serviceMock, Service.class));
		assertTrue(search.containsAnyDoubleMatching(serviceMock, OtherService.class));
		assertTrue(search.containsAnyDoubleMatching(otherServiceMock, OtherService.class));
		assertTrue(search.containsAnyDoubleMatching(otherServiceMock, Service.class));
	}

	@Test
	public void should_throw_exception_when_no_definition_found_by_name_and_class() {
		//given
		final DoubleSearch search = new DoubleSearch(emptyList());
		exception.expect(IllegalStateException.class);
		exception.expectMessage(Matchers.allOf(
				startsWith(MISSING_DOUBLE_EXCEPTION_PREFIX),
				endsWith(NO_DEFINITION_FOUND_EXCEPTION_MESSAGE)));

		//when
		search.findOneDefinition("non existing name", Service.class);
	}

	@Test
	public void should_throw_exception_when_multiple_definitions_found_by_name_and_class() {
		//given
		final String mockName = "mock";
		final Class<?> mockClass = Service.class;
		final DoubleSearch search = new DoubleSearch(asList(
				doubleDefinition(mockClass, mockName),
				doubleDefinition(mockClass, mockName)));
		exception.expect(IllegalStateException.class);
		exception.expectMessage(Matchers.allOf(
				startsWith(MISSING_DOUBLE_EXCEPTION_PREFIX),
				containsString(MULTIPLE_DOUBLES_EXCEPTION_MESSAGE)));

		//when
		search.findOneDefinition(mockName, Service.class);
	}

	@Test
	public void should_throw_exception_when_no_definition_found_by_name() {
		//given
		final DoubleSearch search = new DoubleSearch(emptyList());
		exception.expect(IllegalStateException.class);
		exception.expectMessage(Matchers.allOf(
				startsWith(MISSING_DOUBLE_EXCEPTION_PREFIX),
				endsWith(NO_DEFINITION_FOUND_EXCEPTION_MESSAGE)));

		//when
		search.findOneDefinition("non existing name");
	}

	@Test
	public void should_throw_exception_when_multiple_definitions_found_by_name() {
		//given
		final String mockName = "mock";
		final Class<?> mockClass = Service.class;
		final DoubleSearch search = new DoubleSearch(asList(
				doubleDefinition(mockClass, mockName),
				doubleDefinition(mockClass, mockName)));
		exception.expect(IllegalStateException.class);
		exception.expectMessage(Matchers.allOf(
				startsWith(MISSING_DOUBLE_EXCEPTION_PREFIX),
				containsString(MULTIPLE_DOUBLES_EXCEPTION_MESSAGE)));

		//when
		search.findOneDefinition(mockName);
	}

	@Test
	public void should_throw_exception_when_no_definition_found_by_class() {
		//given
		final DoubleSearch search = new DoubleSearch(emptyList());
		exception.expect(IllegalStateException.class);
		exception.expectMessage(Matchers.allOf(
				startsWith(MISSING_DOUBLE_EXCEPTION_PREFIX),
				endsWith(NO_DEFINITION_FOUND_EXCEPTION_MESSAGE)));

		//when
		search.findOneDefinition(Service.class);
	}

	@Test
	public void should_throw_exception_when_multiple_definitions_found_by_class() {
		//given
		final String mockName = "mock";
		final Class<?> mockClass = Service.class;
		final DoubleSearch search = new DoubleSearch(asList(
				doubleDefinition(mockClass, mockName),
				doubleDefinition(mockClass, mockName)));
		exception.expect(IllegalStateException.class);
		exception.expectMessage(Matchers.allOf(
				startsWith(MISSING_DOUBLE_EXCEPTION_PREFIX),
				containsString(MULTIPLE_DOUBLES_EXCEPTION_MESSAGE)));

		//when
		search.findOneDefinition(mockClass);
	}

	private static class Service {
	}

	private static class OtherService {
	}
}