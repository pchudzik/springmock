package com.pchudzik.springmock.infrastructure.test.infrastructure;

import com.pchudzik.springmock.infrastructure.definition.DoubleDefinition;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class MatchingDoubleFactoryTest {
	private static final Object ANY_BEAN = null;

	@Test
	public void should_match_mock_using_predicate() {
		//given
		final Object firstMock = new Object();
		final Object secondMock = new Object();
		final MatchingDoubleFactory doubleFactory = MatchingDoubleFactory.builder()
				.mock(firstMock, def -> false)
				.mock(secondMock, def -> true)
				.build();

		//expect
		assertEquals(
				secondMock,
				doubleFactory.createMock(Mockito.mock(DoubleDefinition.class)));
	}

	@Test
	public void should_match_spy_using_predicate() {
		//given
		final Object firstSpy = new Object();
		final Object secondSpy = new Object();
		final MatchingDoubleFactory doubleFactory = MatchingDoubleFactory.builder()
				.spy(firstSpy, (obj, def) -> false)
				.spy(secondSpy, (obj, def) -> true)
				.build();

		//expect
		assertEquals(
				secondSpy,
				doubleFactory.createSpy(ANY_BEAN, Mockito.mock(DoubleDefinition.class)));
	}

	@Test
	public void should_return_first_mock_matching_predicate() {
		//given
		final Object firstMock = new Object();
		final Object secondMock = new Object();
		final MatchingDoubleFactory doubleFactory = MatchingDoubleFactory.builder()
				.mock(firstMock, def -> true)
				.mock(secondMock, def -> true)
				.build();

		//expect
		assertEquals(
				firstMock,
				doubleFactory.createMock(Mockito.mock(DoubleDefinition.class)));
	}

	@Test
	public void should_return_first_spy_matching_predicate() {
		//given
		final Object firstSpy = new Object();
		final Object secondSpy = new Object();
		final MatchingDoubleFactory doubleFactory = MatchingDoubleFactory.builder()
				.spy(firstSpy, (obj, def) -> true)
				.spy(secondSpy, (obj, def) -> true)
				.build();

		//expect
		assertEquals(
				firstSpy,
				doubleFactory.createSpy(ANY_BEAN, Mockito.mock(DoubleDefinition.class)));
	}

	@Test
	public void should_throw_exception_if_no_mock_matched() {
		final MatchingDoubleFactory doubleFactory = MatchingDoubleFactory.builder().build();

		try {
			doubleFactory.createMock(Mockito.mock(DoubleDefinition.class));
			fail("should fail");
		} catch (NoSuchElementException ex) {
			assertNotNull(ex);
		}
	}

	@Test
	public void should_throw_exception_if_no_spy_matched() {
		final MatchingDoubleFactory doubleFactory = MatchingDoubleFactory.builder().build();

		try {
			doubleFactory.createSpy(ANY_BEAN, Mockito.mock(DoubleDefinition.class));
			fail("should fail");
		} catch (NoSuchElementException ex) {
			assertNotNull(ex);
		}
	}
}