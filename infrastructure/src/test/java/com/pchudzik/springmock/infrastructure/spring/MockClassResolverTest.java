package com.pchudzik.springmock.infrastructure.spring;

import com.pchudzik.springmock.infrastructure.definition.DoubleDefinition;
import com.pchudzik.springmock.infrastructure.definition.registry.DoubleRegistry;
import org.junit.Test;

import static com.pchudzik.springmock.infrastructure.definition.DoubleDefinitionTestFactory.doubleDefinition;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;

public class MockClassResolverTest {
	private static final String beanName = "any bean name";
	private static Class<?> ANY_CLASS = null;

	@Test
	public void should_resolve_bean_class_for_mocked_bean() {
		final MockClassResolver mockClassResolver = createClassResolver(doubleDefinition(MockClass.class, beanName));

		//expect
		assertEquals(
				MockClass.class,
				mockClassResolver.predictBeanType(ANY_CLASS, beanName));
	}

	@Test
	public void should_return_not_predictable_type_when_mock_does_not_exists_in_double_registry() {
		//given
		final MockClassResolver mockClassResolver = createClassResolver(doubleDefinition(OtherMockClass.class));

		//expect
		assertEquals(
				null,
				mockClassResolver.predictBeanType(ANY_CLASS, beanName));
	}

	private MockClassResolver createClassResolver(DoubleDefinition... mocks) {
		final DoubleRegistry doubleRegistry = new DoubleRegistry(asList(mocks), emptyList());
		return new MockClassResolver(doubleRegistry);
	}

	private static class MockClass {
	}

	private static class OtherMockClass {
	}
}