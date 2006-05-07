package com.teaminabox.eclipse.wiki.editors;

import org.eclipse.jface.text.contentassist.ICompletionProposal;

import com.teaminabox.eclipse.wiki.WikiTest;

public final class WikiCompletionProcessorTest extends WikiTest {

	private WikiEditor	editor;

	public WikiCompletionProcessorTest(String name) {
		super(name);
	}

	public void testEclipsePathOverridesJavaType() {
        String projectName = project.getName(); // WikiTest.TEST_PROJECT;
        assertTrue(projectName.matches(WikiTest.TEST_PROJECT+"\\d+"));
		String wikiContent = "Eclipse:/" + projectName;
        ICompletionProposal[] proposals = prepare("package " + projectName + ".foo.bar; \npublic class Test{}", projectName + "/foo/bar/Test.java", wikiContent, wikiContent.length());
		assertEquals("one proposal", 1, proposals.length);
        assertEquals("Eclipse proposal", proposals[0].getDisplayString(), projectName);
	}

	private ICompletionProposal[] prepare(String code, String path, String wikiContents, int cursorPosition) {
		create(code, path);
		editor = createWikiDocumentAndOpen(wikiContents).getEditor();
		return new WikiCompletionProcessor(editor).computeCompletionProposals(editor.getTextViewerForTest(), cursorPosition);
	}
}
