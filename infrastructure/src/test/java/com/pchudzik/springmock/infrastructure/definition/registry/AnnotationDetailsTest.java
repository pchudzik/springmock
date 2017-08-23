package com.pchudzik.springmock.infrastructure.definition.registry;

import com.pchudzik.springmock.infrastructure.annotation.AutowiredMock;
import com.pchudzik.springmock.infrastructure.annotation.AutowiredSpy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mockito;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class AnnotationDetailsTest {
	private static final Function<AutowiredMock, AnnotationDetails> mockCreator = AnnotationDetails::mock;
	private static final Function<AutowiredSpy, AnnotationDetails> spyCreator = AnnotationDetails::spy;

	@RunWith(Parameterized.class)
	public static class MissingNameTest {
		@Parameters
		public static Collection<Object[]> params() {
			return asList(
					new Object[]{mockCreator, new AutowiredMockBuilder().build()},
					new Object[]{mockCreator, new AutowiredMockBuilder().name("").build()},
					new Object[]{mockCreator, new AutowiredMockBuilder().name("  ").build()},
					new Object[]{mockCreator, new AutowiredMockBuilder().name(null).build()},
					new Object[]{spyCreator, new AutowiredSpyBuilder().build()},
					new Object[]{spyCreator, new AutowiredSpyBuilder().name("").build()},
					new Object[]{spyCreator, new AutowiredSpyBuilder().name("  ").build()},
					new Object[]{spyCreator, new AutowiredSpyBuilder().name(null).build()}
			);
		}

		@Parameter(0)
		public Function<Annotation, AnnotationDetails> mockFactory;

		@Parameter(1)
		public Annotation annotation;

		@Test
		public void should_detect_missing_mock_name() {
			final AnnotationDetails details = mockFactory.apply(annotation);

			assertEquals(Optional.empty(), details.getName());
		}
	}

	public static class NameExtractionTest {
		@Test
		public void should_detect_existing_mock_name() {
			final AnnotationDetails details = AnnotationDetails.mock(new AutowiredMockBuilder().name("a name").build());

			assertEquals(
					Optional.of("a name"),
					details.getName());
		}

		@Test
		public void should_detect_existing_spy_name() {
			final AnnotationDetails details = AnnotationDetails.spy(new AutowiredSpyBuilder().name("a name").build());

			assertEquals(
					Optional.of("a name"),
					details.getName());
		}
	}

	@RunWith(Parameterized.class)
	public static class MissingAliasesTest {
		@Parameters
		public static Collection<Object[]> params() {
			return asList(
					new Object[]{mockCreator, new AutowiredMockBuilder().build()},
					new Object[]{spyCreator, new AutowiredSpyBuilder().build()}
			);
		}

		@Parameter(0)
		public Function<Annotation, AnnotationDetails> mockFactory;

		@Parameter(1)
		public Annotation annotation;

		@Test
		public void should_detect_missing_mock_name() {
			final AnnotationDetails details = mockFactory.apply(annotation);

			assertEquals(Optional.empty(), details.getName());
		}
	}

	public static class AliasExtractionTest {
		@Test
		public void should_detect_existing_mock_alias() {
			final AnnotationDetails details = AnnotationDetails.mock(new AutowiredMockBuilder()
					.alias("alias 1", "alias 2")
					.build());

			assertEquals(
					new HashSet<>(asList("alias 1", "alias 2")),
					details.getAlias());
		}

		@Test
		public void should_detect_existing_spy_alias() {
			final AnnotationDetails details = AnnotationDetails.spy(new AutowiredSpyBuilder()
					.alias("alias 1", "alias 2")
					.build());

			assertEquals(
					new HashSet<>(asList("alias 1", "alias 2")),
					details.getAlias());
		}
	}

	public static class DoubleConfigurationResolverTest {
		private static final String anyName = "any name";
		private static final Field anyField = ReflectionUtils.findField(DoubleConfigurationResolverTest.class, "anyName");

		private DoubleConfigurationResolver configurationResolver = Mockito.mock(DoubleConfigurationResolver.class);

		@Test
		public void should_resolve_mock_configuration() {
			//given
			final AnnotationDetails details = AnnotationDetails.mock(new AutowiredMockBuilder().build());

			//when
			details
					.resolveConfiguration(configurationResolver)
					.apply(anyName, anyField);

			//then
			Mockito
					.verify(configurationResolver)
					.resolveMockConfiguration(anyName, anyField);
		}

		@Test
		public void should_resolve_spy_configuration() {
			//given
			final AnnotationDetails details = AnnotationDetails.spy(new AutowiredSpyBuilder().build());

			//when
			details
					.resolveConfiguration(configurationResolver)
					.apply(anyName, anyField);

			//then
			Mockito
					.verify(configurationResolver)
					.resolveSpyConfiguration(anyName, anyField);
		}
	}
}