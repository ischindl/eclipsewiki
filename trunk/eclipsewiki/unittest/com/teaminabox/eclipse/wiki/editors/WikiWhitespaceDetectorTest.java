package com.teaminabox.eclipse.wiki.editors;

import com.teaminabox.eclipse.wiki.util.WikiWhitespaceDetector;

import junit.framework.TestCase;

public class WikiWhitespaceDetectorTest extends TestCase {

	public void testIsWhiteSpaceCharacter() {
		assertTrue("\\n", WikiWhitespaceDetector.isWhiteSpaceCharacter('\n'));
		assertTrue("space", WikiWhitespaceDetector.isWhiteSpaceCharacter(' '));
		assertTrue("tab", WikiWhitespaceDetector.isWhiteSpaceCharacter('\t'));
		assertTrue("\\r", WikiWhitespaceDetector.isWhiteSpaceCharacter('\r'));
		assertFalse("non whitespace", WikiWhitespaceDetector.isWhiteSpaceCharacter('a'));
	}

	public void testIndexOfWhiteSpace() {
		assertEquals("starts with space", 0, WikiWhitespaceDetector.indexOfWhiteSpace(" a"));
		assertEquals("ends with space", 1, WikiWhitespaceDetector.indexOfWhiteSpace("a\r"));
		assertEquals("has space", 1, WikiWhitespaceDetector.indexOfWhiteSpace("b a"));
		assertEquals("no space", -1, WikiWhitespaceDetector.indexOfWhiteSpace("a"));
	}

}
