package com.teaminabox.eclipse.wiki.util;

import static com.teaminabox.eclipse.wiki.util.Strings.deCamelCase;
import junit.framework.TestCase;

public class StringsTest extends TestCase {

	public void testDeCamelCase() {
		assertEquals("Foo Bar", deCamelCase("FooBar"));
	}

	public void testDeCamelCaseForForcedLink() {
		assertEquals("Forced Link", deCamelCase("Forced Link"));
	}
}
