package com.teaminabox.eclipse.wiki.renderer;


import junit.framework.TestCase;

public class LinkMakerTest extends TestCase {
	
	private TestLinkMaker linkMaker = new TestLinkMaker();

	public void testGetLinkForUrl() throws Exception {
		assertEquals("<a href=\"http://www.google.com\">Google</a>", linkMaker.getLink("http://www.google.com", "Google"));
	}

	public void testGetLinkForImage() throws Exception {
		assertEquals("<img alt=\"Google\" src=\"http://a.link.to/an/image.jpg\"/>", linkMaker.getLink("http://a.link.to/an/image.jpg", "Google"));
	}

}
