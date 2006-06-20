package com.teaminabox.eclipse.wiki.text;

import junit.framework.TestCase;

public final class ForcedLinkTextRegionTest extends TestCase {

	public void testGetWikiDocumentNameForTwoBrackets() {
		ForcedLinkTextRegion region = new ForcedLinkTextRegion("[[foo]]", 2);
		assertEquals("foo", region.getWikiDocumentName());
		assertEquals("foo", region.getDisplayText());
	}

	public void testGetWikiDocumentNameForOneBracket() {
		ForcedLinkTextRegion region = new ForcedLinkTextRegion("[foo]", 1);
		assertEquals("foo", region.getWikiDocumentName());
		assertEquals("foo", region.getDisplayText());
	}

	public void testGetWikiDocumentNameWithSpaceSeparatedName() {
		ForcedLinkTextRegion region = new ForcedLinkTextRegion("[[foo bar ]]", 2);
		assertEquals("foobar", region.getWikiDocumentName());
		assertEquals("foo bar ", region.getDisplayText());
	}

}
