package com.teaminabox.eclipse.wiki.renderer;

import java.io.IOException;
import java.util.Arrays;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.WikiPlugin;
import com.teaminabox.eclipse.wiki.WikiTest;
import com.teaminabox.eclipse.wiki.editors.WikiBrowserEditor;
import com.teaminabox.eclipse.wiki.editors.WikiDocumentContext;

public abstract class AbstractContentRendererTest extends WikiTest {

	private static final String	CLASS_SOURCE	= "package com.teaminabox.foo;\npublic class BigClass { class InnerClass {} }";
	private static final String	CLASS_NAME		= "com.teaminabox.foo.BigClass";

	public AbstractContentRendererTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		create(AbstractContentRendererTest.CLASS_SOURCE, AbstractContentRendererTest.CLASS_NAME.replaceAll("\\.", "/") + ".java");
	}

	protected abstract AbstractContentRenderer getRenderer();

	public void testReplacePair() {
		AbstractContentRenderer renderer = getRenderer();
		assertEquals("XfooY", renderer.replacePair("|||foo|||", "|||", "X", "Y"));
		assertEquals("XfooY ", renderer.replacePair("|||foo||| ", "|||", "X", "Y"));
		assertEquals(" XfooY ", renderer.replacePair(" |||foo||| ", "|||", "X", "Y"));

		assertEquals("_foo", renderer.replacePair("_foo", "_", "X", "Y"));
		assertEquals("foo_", renderer.replacePair("foo_", "_", "X", "Y"));
		assertEquals("foo", renderer.replacePair("foo", "_", "X", "Y"));
		assertEquals("XfooY", renderer.replacePair("*foo*", "*", "X", "Y"));
		assertEquals("*", renderer.replacePair("*", "*", "X", "Y"));
		assertEquals("", renderer.replacePair("", "*", "X", "Y"));
	}

	public void testFunctional() throws IOException {
		String functionalTest = getFunctionalTestFileName();
		String content = load(functionalTest + ".wiki");
		String expected = load(functionalTest + ".expected");
		WikiPlugin.getDefault().getPluginPreferences().setValue(WikiConstants.BROWSER_RENDERER, getRenderer().getClass().getName());
		WikiBrowserEditor editor = createWikiDocumentAndOpen(content, functionalTest + ".wiki");
		WikiDocumentContext context = editor.getEditor().getContext();
		String html = getRenderer().render(context, new IdeLinkMaker(context));
		assertEquals(expected, html);
	}

	protected final String getFunctionalTestFileName() {
		String name = getClass().getName();
		return name.substring(name.lastIndexOf('.') + 1);
	}

	public void testSplit() {
		assertTrue(Arrays.equals(new String[] { "a", "b", "", "c", "d" }, getRenderer().split("|a|b||c|d|", "|")));
	}

	protected void assertRenderedContains(String markup, String fragment) {
		assertTrue(getHtml(markup).indexOf(fragment) >= 0);
	}

	protected String getHtml(String text) {
		WikiBrowserEditor editor = createWikiDocumentAndOpen(text);
		WikiDocumentContext context = editor.getEditor().getContext();
		String html = getRenderer().render(context, new IdeLinkMaker(context));
		return html.replaceAll("\\n", "");
	}
}
