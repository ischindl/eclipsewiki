package com.teaminabox.eclipse.wiki.text;

import junit.framework.TestCase;

public class WikiLinkTextRegionTest extends TestCase {

	public void testDeCamelCase() {
		assertEquals("Foo Bar", WikiLinkTextRegion.deCamelCase("FooBar"));
	}

	public void testDeCamelCaseForForcedLink() {
		assertEquals("Forced Link", WikiLinkTextRegion.deCamelCase("Forced Link"));
	}
}
