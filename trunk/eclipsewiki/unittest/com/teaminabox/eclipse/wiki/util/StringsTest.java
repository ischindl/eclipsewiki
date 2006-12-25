package com.teaminabox.eclipse.wiki.util;

import static com.teaminabox.eclipse.wiki.util.Strings.deCamelCase;
import static org.junit.Assert.assertEquals;

import org.junit.*;

public class StringsTest {

	@Test
	public void testDeCamelCase() {
		assertEquals("Foo Bar", deCamelCase("FooBar"));
	}

	@Test
	public void testDeCamelCaseForForcedLink() {
		assertEquals("Forced Link", deCamelCase("Forced Link"));
	}
}
