package com.teaminabox.eclipse.wiki.editors;

import java.util.Iterator;
import java.util.Map;

import junit.framework.Assert;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;

import com.teaminabox.eclipse.wiki.WikiTest;
import com.teaminabox.eclipse.wiki.preferences.WikiPreferences;

public final class WikiEditorTest extends WikiTest {

	private static final String	WIKI_CONTENT	= "A Test for WikiEditor. SecondLink. more text";

	private WikiEditor			editor;

	public WikiEditorTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		editor = createWikiDocumentAndOpen(WikiEditorTest.WIKI_CONTENT).getEditor();
	}

	public void testGetDocumentText() {
		Assert.assertEquals(WikiEditorTest.WIKI_CONTENT, editor.getDocumentText());
	}

	public void testGetFileForWikiName() {
		createWikiDocumentAndOpen("test", "AnotherFile.wiki");
		IFile file = editor.getContext().getFileForWikiName("AnotherFile");
		Assert.assertNotNull(file);
		Assert.assertEquals("AnotherFile.wiki", file.getName());
	}

	public void testGetWikiNameBeingEdited() {
		Assert.assertEquals(WIKI_FILE.substring(0, WIKI_FILE.indexOf(".")), editor.getContext().getWikiNameBeingEdited());
	}

	public void testGetWikiSpaceNoLocalSpace() {
		Map received = editor.getContext().getWikiSpace();
		Map expected = WikiPreferences.getWikiSpace();
		Assert.assertEquals("size", expected.size(), received.size());
		Iterator iterator = expected.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			Assert.assertTrue(key, received.containsKey(key));
			Assert.assertEquals("value", expected.get(key), received.get(key));
		}
	}

	public void testGetWikiSpaceLocalSpace() {
		create("TestSpace=foo", "wikispace.properties");
		Map space = editor.getContext().getWikiSpace();
		Assert.assertTrue("key", space.containsKey("TestSpace"));
		Assert.assertEquals("value", "foo", space.get("TestSpace"));
	}

	public void testGetWikiSpaceLinkAfterLocalWikiSpaceDeleted() throws InterruptedException {
		create("TestSpace=foo", "wikispace.properties");
		/* give event thread time to run so that the editor wakes up */
		Thread.sleep(200);
		Map space = editor.getContext().getWikiSpace();
		Assert.assertTrue("key", space.containsKey("TestSpace"));

		try {
			delete("wikispace.properties");
	        space = editor.getContext().getWikiSpace();
	        Assert.assertFalse("key", space.containsKey("TestSpace"));
		} catch (RuntimeException e) {
			// Do nothing - Windows can't delete properties file;
		}
	}

	public void testGetWikiSpaceLink() {
		create("TestSpace=foo", "wikispace.properties");
		Assert.assertEquals("value", "foo", editor.getContext().getWikiSpaceLink("TestSpace"));
	}

	public void testGetWorkingLocation() {
		IContainer container = editor.getContext().getWorkingLocation();
        Assert.assertTrue(container.getName().matches(TEST_PROJECT+"\\d*"));
	}

	public void testNavigateToNextLink() {
		editor.selectAndReveal(0, 0);
		editor.navigateToNextLink();
		int current = getCursorPosition(editor);
		Assert.assertEquals(WikiEditorTest.WIKI_CONTENT.indexOf("WikiEditor"), current);
	}

	public void testNavigateToPreviousLink() {
		editor.selectAndReveal(WikiEditorTest.WIKI_CONTENT.length(), 0);
		editor.navigateToPreviousLink();
		int current = getCursorPosition(editor);
		Assert.assertEquals(WikiEditorTest.WIKI_CONTENT.indexOf("SecondLink"), current);
	}

	public void testOpenWikiLinkOnSelection() {
		editor.selectAndReveal(0, 0);
		editor.navigateToNextLink();
		editor.openWikiLinkOnSelection();
		Assert.assertTrue(exists("WikiEditor.wiki"));
	}
}
