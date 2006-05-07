package com.teaminabox.eclipse.wiki.editors;

import junit.framework.TestCase;

public class PathWithLineNumberTest extends TestCase {

	public void testPathWithoutLineNumber() throws Exception {
		assertEquals("a/b", 0, "a/b");
	}

	public void testPathWithLineNumber() throws Exception {
		assertEquals("a/b:10", 9, "a/b");
	}
	
	private void assertEquals(String path, int expectedLineNumber, String expectedPath) {
		PathWithLineNumber pathWithLineNumber = new PathWithLineNumber(path);
		assertEquals(expectedLineNumber, pathWithLineNumber.getLine());
		assertEquals(expectedPath, pathWithLineNumber.getPath().toString());
	}
}
