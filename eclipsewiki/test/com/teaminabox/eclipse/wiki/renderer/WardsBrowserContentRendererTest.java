package com.teaminabox.eclipse.wiki.renderer;


public final class WardsBrowserContentRendererTest extends AbstractContentRendererTest {

	protected AbstractContentRenderer getRenderer() {
		return new WardsBrowserContentRenderer();
	}

	public void testIsHeaderWithHeader() {
		assertTrue(getRenderer().isHeader("'''foo'''"));
	}

	public void testIsHeaderWithHeaderMarkupFollowedByText() {
		assertFalse(getRenderer().isHeader("'''foo'''" + " foo"));
	}

	public void testIsHeaderWithEmphasis() {
		assertFalse(getRenderer().isHeader("'''''foo'''''"));
	}

	/**
	 * Test for [ 1089118 ] WardsWiki: markup in quote section is ignored.
	 */
	public void testQuoteWithMarkup() {
		String markup = "\t :\t'''foo'''";
		String expected = "<p class=\"quote\"><b>foo</b></p>";
		assertRenderedContains(markup, expected);
	}

	public void testWhiteSpacePreservedInMonospace() {
		String markup = " foo bar  ";
		String expected = "<pre class=\"monospace\"> foo bar  </pre>";
		assertRenderedContains(markup, expected);
	}
	
}
