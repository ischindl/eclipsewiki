package com.teaminabox.eclipse.wiki.editors.completion;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.junit.Test;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.WikiPlugin;
import com.teaminabox.eclipse.wiki.WikiTest;
import com.teaminabox.eclipse.wiki.editors.WikiEditor;

public final class JavaCompletionProcessorTest extends WikiTest {

	private static final String	TYPE_PATH		= "foo/bar/Test.java";
	private static final String	TYPE_CONTENTS	= "package foo.bar;\npublic interface Test {\n}";

	@Test
	public void testGetProposalsNoContent() throws Exception {
		WikiEditor editor = createWikiDocumentAndOpen("").getEditor();
		JavaCompletionProcessor processor = new JavaCompletionProcessor();

		ArrayList<ICompletionProposal> proposals = processor.getProposals(getJavaProject(), editor.getTextViewerForTest(), 0);
		assertEquals(0, proposals.size());
	}

	@Test
	public void testGetClassProposalDefaultPackage() throws Exception {
		ArrayList<ICompletionProposal> proposals = prepare("public class Test {\n}", "Test.java", "T", 1);
		assertEquals(1, proposals.size());
		ICompletionProposal proposal = proposals.get(0);
		assertEquals("Test - (default package)", proposal.getDisplayString());
		assertEquals("Image", WikiPlugin.getDefault().getImageRegistry().get(WikiConstants.CLASS_ICON), proposal.getImage());
	}

	@Test
	public void testGetPackageProposal() throws Exception {
		ArrayList<ICompletionProposal> proposals = prepare(JavaCompletionProcessorTest.TYPE_CONTENTS, JavaCompletionProcessorTest.TYPE_PATH, "f", 1);
		assertEquals(2, proposals.size());
		ICompletionProposal proposal = proposals.get(0);

		assertEquals("foo", proposal.getDisplayString());
		assertEquals("Image", WikiPlugin.getDefault().getImageRegistry().get(WikiConstants.PACKAGE_ICON), proposal.getImage());

		proposal = proposals.get(1);
		assertEquals("foo.bar", proposal.getDisplayString());

		assertEquals("Image", WikiPlugin.getDefault().getImageRegistry().get(WikiConstants.PACKAGE_ICON), proposal.getImage());
	}

	@Test
	public void testGetPackageProposalWithPartitialPackageName() throws Exception {
		ArrayList<ICompletionProposal> proposals = prepare(JavaCompletionProcessorTest.TYPE_CONTENTS, JavaCompletionProcessorTest.TYPE_PATH, "foo.ba", 6);
		assertEquals(1, proposals.size());
		ICompletionProposal proposal = proposals.get(0);
		assertEquals("foo.bar", proposal.getDisplayString());
		assertEquals("Image", WikiPlugin.getDefault().getImageRegistry().get(WikiConstants.PACKAGE_ICON), proposal.getImage());
	}

	@Test
	public void testGetTypeProposalInPackage() throws Exception {
		ArrayList<ICompletionProposal> proposals = prepare(JavaCompletionProcessorTest.TYPE_CONTENTS, JavaCompletionProcessorTest.TYPE_PATH, "foo.bar.", 8);
		assertEquals(1, proposals.size());
		ICompletionProposal proposal = proposals.get(0);
		assertEquals("Test - foo.bar", proposal.getDisplayString());
	}

	@Test
	public void testGetTypeProposalInSentence() throws Exception {
		ArrayList<ICompletionProposal> proposals = prepare(JavaCompletionProcessorTest.TYPE_CONTENTS, JavaCompletionProcessorTest.TYPE_PATH, "a test foo.bar. thing", 15);
		assertEquals(1, proposals.size());
		ICompletionProposal proposal = proposals.get(0);
		assertEquals("Test - foo.bar", proposal.getDisplayString());
	}

	@Test
	public void testGetTypePrefixedWithNonJavaCharacters() throws Exception {
		ArrayList<ICompletionProposal> proposals = prepare(JavaCompletionProcessorTest.TYPE_CONTENTS, JavaCompletionProcessorTest.TYPE_PATH, "'''T", 4);
		assertEquals(1, proposals.size());
		ICompletionProposal proposal = proposals.get(0);
		assertEquals("Test - foo.bar", proposal.getDisplayString());
	}

	@Test
	public void testGetTypePrefixedWithFullStop() throws Exception {
		ArrayList<ICompletionProposal> proposals = prepare(JavaCompletionProcessorTest.TYPE_CONTENTS, JavaCompletionProcessorTest.TYPE_PATH, ".T", 2);
		assertEquals(1, proposals.size());
		ICompletionProposal proposal = proposals.get(0);
		assertEquals("Test - foo.bar", proposal.getDisplayString());
	}

	@Test
	public void testGetTypePrefixedWithSpace() throws Exception {
		ArrayList<ICompletionProposal> proposals = prepare(JavaCompletionProcessorTest.TYPE_CONTENTS, JavaCompletionProcessorTest.TYPE_PATH, " T", 2);
		assertEquals(1, proposals.size());
		ICompletionProposal proposal = proposals.get(0);
		assertEquals("Test - foo.bar", proposal.getDisplayString());
	}

	@Test
	public void testTypeInPackage() throws Exception {
		ArrayList<ICompletionProposal> proposals = prepare(JavaCompletionProcessorTest.TYPE_CONTENTS, JavaCompletionProcessorTest.TYPE_PATH, "a test Test thing", 11);
		assertEquals(1, proposals.size());
		ICompletionProposal proposal = proposals.get(0);
		assertEquals("Test - foo.bar", proposal.getDisplayString());
	}

	@Test
	public void testCursorOnSpace() throws Exception {
		ArrayList<ICompletionProposal> proposals = prepare(JavaCompletionProcessorTest.TYPE_CONTENTS, JavaCompletionProcessorTest.TYPE_PATH, "a Test", 1);
		assertEquals(0, proposals.size());
	}

	@Test
	public void testIsCandidateWhenWhiteSpace() throws Exception {
		ArrayList<ICompletionProposal> proposals = prepare(JavaCompletionProcessorTest.TYPE_CONTENTS, JavaCompletionProcessorTest.TYPE_PATH, " ", 1);
		assertEquals(0, proposals.size());
	}

	@Test
	public void testGetTypeProposalsWhenDotPreceededByWhiteSpace() throws Exception {
		ArrayList<ICompletionProposal> proposals = prepare(JavaCompletionProcessorTest.TYPE_CONTENTS, JavaCompletionProcessorTest.TYPE_PATH, " .", 2);
		assertEquals(0, proposals.size());
	}

	private ArrayList<ICompletionProposal> prepare(String code, String path, String wikiContents, int cursorPosition) throws BadLocationException, CoreException {
		createAndOpen(code, path);
		WikiEditor editor = createWikiDocumentAndOpen(wikiContents).getEditor();
		return new JavaCompletionProcessor().getProposals(getJavaProject(), editor.getTextViewerForTest(), cursorPosition);
	}
}
