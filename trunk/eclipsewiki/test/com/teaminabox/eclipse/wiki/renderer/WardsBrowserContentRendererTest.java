package com.teaminabox.eclipse.wiki.renderer;

public final class WardsBrowserContentRendererTest extends AbstractContentRendererTest {

	public WardsBrowserContentRendererTest(String name) {
		super(name);
	}

	protected AbstractContentRenderer getRenderer() {
		return new WardsBrowserContentRenderer();
	}

	public void testIsHeaderWithHeader() {
		assertTrue(getRenderer().isHeader(WardsBrowserContentRenderer.HEADER_MARKUP + "foo" + WardsBrowserContentRenderer.HEADER_MARKUP));
	}

	public void testIsHeaderWithHeaderMarkupFollowedByText() {
		assertFalse(getRenderer().isHeader(WardsBrowserContentRenderer.HEADER_MARKUP + "foo" + WardsBrowserContentRenderer.HEADER_MARKUP + " foo"));
	}

	public void testIsHeaderWithEmphasis() {
		assertFalse(getRenderer().isHeader(WardsBrowserContentRenderer.EMPHASIS_MARKUP + "foo" + WardsBrowserContentRenderer.EMPHASIS_MARKUP));
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
