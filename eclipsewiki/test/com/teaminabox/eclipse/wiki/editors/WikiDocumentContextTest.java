package com.teaminabox.eclipse.wiki.editors;

import org.eclipse.core.resources.IResource;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.WikiTest;
import com.teaminabox.eclipse.wiki.preferences.WikiPreferences;
import com.teaminabox.eclipse.wiki.text.WikiNameTextRegion;
import com.teaminabox.eclipse.wiki.text.WikiLinkTextRegion;

public final class WikiDocumentContextTest extends WikiTest {

	private static final String	WIKIDOC	= "WikiDoc";
	private WikiDocumentContext	context;

	public WikiDocumentContextTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		WikiBrowserEditor editor = createWikiDocumentAndOpen("", WikiDocumentContextTest.WIKIDOC + WikiConstants.WIKI_FILE_EXTENSION);
		context = editor.getEditor().getContext();
	}

	public void testGetWorkingLocation() {
		assertEquals(getJavaProject().getResource(), context.getWorkingLocation());
	}

	public void testGetFileForWikiNameWithNoFile() {
		context.getFileForWikiName("FooBar");
		IResource resource = getJavaProject().getProject().findMember("FooBar" + WikiConstants.WIKI_FILE_EXTENSION);
		assertTrue(resource == null);
	}

	public void testGetFileForWikiName() {
		create("", "FooBar" + WikiConstants.WIKI_FILE_EXTENSION);
		context.getFileForWikiName("FooBar");
		IResource resource = getJavaProject().getProject().findMember("FooBar" + WikiConstants.WIKI_FILE_EXTENSION);
		assertTrue(resource != null && resource.exists());
	}

	public void testGetWikiNameBeingEdited() {
		assertEquals(WikiDocumentContextTest.WIKIDOC, context.getWikiNameBeingEdited());
	}

	public void testHasWikiSiblingWhenFalse() {
		WikiLinkTextRegion region = new WikiNameTextRegion("FooBar");
		assertFalse(context.hasWikiSibling(region));
	}

	public void testHasWikiSiblingWhenTrue() {
		create("", "FooBar" + WikiConstants.WIKI_FILE_EXTENSION);
		WikiLinkTextRegion region = new WikiNameTextRegion("FooBar");
		assertTrue(context.hasWikiSibling(region));
	}

	public void testGetWikiSpaceWithNoLocalSpace() {
		assertEquals(WikiPreferences.getWikiSpace().size(), context.getWikiSpace().size());
		assertTrue(WikiPreferences.getWikiSpace().keySet().containsAll(context.getWikiSpace().keySet()));
	}

	public void testGetWikiSpaceWithLocalSpace() {
		create("A=B", "wikispace.properties");
		assertEquals(WikiPreferences.getWikiSpace().size() + 1, context.getWikiSpace().size());
		assertTrue(context.getWikiSpace().keySet().containsAll(WikiPreferences.getWikiSpace().keySet()));
		assertTrue(context.getWikiSpace().containsKey("A"));
	}

	public void testGetWikiSpaceLinkWithNoLocalSpace() {
		assertTrue(context.getWikiSpaceLink("nothing there") == null);
	}

	public void testGetWikiSpaceLinkWithLocalSpace() {
		create("A=B", "wikispace.properties");
		assertEquals("B", context.getWikiSpaceLink("A"));
	}

	public void testIsExcluded() {
		create("IgnoreMe", "wiki.exclude");
		assertTrue(context.isExcluded("IgnoreMe"));
	}

}
