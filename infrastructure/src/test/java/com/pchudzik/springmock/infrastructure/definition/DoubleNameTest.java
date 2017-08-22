package com.pchudzik.springmock.infrastructure.definition;

import org.junit.Test;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DoubleNameTest {
	@Test
	public void names_with_different_names_nas_aliases_should_not_be_equal() {
		//given
		final DoubleName aName = new DoubleName("a", asList("b", "c"));
		final DoubleName otherName = new DoubleName("1", asList("2", "3"));

		//expect
		assertFalse(aName.equals(otherName));
	}

	@Test
	public void names_with_the_same_name_and_different_aliases_should_not_be_equal() {
		//given
		final DoubleName name = new DoubleName("a", asList("b", "c"));
		final DoubleName otherName = new DoubleName("a", asList("2", "3"));

		//expect
		assertFalse(name.equals(otherName));
	}

	@Test
	public void names_with_the_same_name_should_be_equal() {
		//given
		final DoubleName a1Name = new DoubleName("a", emptyList());
		final DoubleName a2Name = new DoubleName("a", emptyList());

		//expect
		assertTrue(a1Name.equals(a2Name));
		assertEquals(a1Name.hashCode(), a2Name.hashCode());
	}

	@Test
	public void names_with_the_same_alias_but_different_name_should_be_equal() {
		//given
		final DoubleName name1 = new DoubleName("a", asList("b", "c"));
		final DoubleName name2 = new DoubleName("b", asList("a", "c"));

		//expect
		assertTrue(name1.equals(name2));
		assertEquals(name1.hashCode(), name2.hashCode());
	}
}
