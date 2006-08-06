package com.teaminabox.eclipse.wiki.text;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.WikiTest;
import com.teaminabox.eclipse.wiki.editors.WikiDocumentContext;
import com.teaminabox.eclipse.wiki.editors.WikiEditor;

public final class TextRegionBuilderTest extends WikiTest {

	private WikiEditor			editor;
	private WikiDocumentContext	context;

	protected void setUp() throws Exception {
		super.setUp();
		editor = createWikiDocumentAndOpen("").getEditor();
		context = editor.getContext();
	}

	public void testGetFirstTextRegionWithExcludedText() {
		create("IgnoreMe", WikiConstants.EXCLUDES_FILE);
		TextRegion firstTextRegion = TextRegionBuilder.getFirstTextRegion("IgnoreMe blah", context);
		assertTrue(firstTextRegion instanceof BasicTextRegion);
		assertEquals("IgnoreMe", firstTextRegion.getText());
	}

}
