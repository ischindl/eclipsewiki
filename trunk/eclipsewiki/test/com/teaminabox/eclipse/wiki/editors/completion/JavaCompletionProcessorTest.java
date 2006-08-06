package com.teaminabox.eclipse.wiki.editors.completion;

import java.util.ArrayList;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ui.PartInitException;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.WikiPlugin;
import com.teaminabox.eclipse.wiki.WikiTest;
import com.teaminabox.eclipse.wiki.editors.WikiEditor;

public final class JavaCompletionProcessorTest extends WikiTest {

	private static final String	TYPE_PATH		= "foo/bar/Test.java";
	private static final String	TYPE_CONTENTS	= "package foo.bar;\npublic interface Test {\n}";

	public void testGetProposalsNoContent() throws Exception {
		WikiEditor editor = createWikiDocumentAndOpen("").getEditor();
		JavaCompletionProcessor processor = new JavaCompletionProcessor();

		ArrayList proposals = processor.getProposals(getJavaProject(), editor.getTextViewerForTest(), 0);
		assertEquals(0, proposals.size());
	}

	public void testGetClassProposalDefaultPackage() throws Exception {
		ArrayList proposals = prepare("public class Test {\n}", "Test.java", "T", 1);
		assertEquals(1, proposals.size());
		ICompletionProposal proposal = (ICompletionProposal) proposals.get(0);
		assertEquals("Test - (default package)", proposal.getDisplayString());
		assertEquals("Image", WikiPlugin.getDefault().getImageRegistry().get(WikiConstants.CLASS_ICON), proposal.getImage());
	}

	public void testGetPackageProposal() throws Exception {
		ArrayList proposals = prepare(JavaCompletionProcessorTest.TYPE_CONTENTS, JavaCompletionProcessorTest.TYPE_PATH, "f", 1);
		assertEquals(2, proposals.size());
		ICompletionProposal proposal = (ICompletionProposal) proposals.get(0);
		
		assertEquals("foo", proposal.getDisplayString());
		assertEquals("Image", WikiPlugin.getDefault().getImageRegistry().get(WikiConstants.PACKAGE_ICON), proposal.getImage());
		
		proposal = (ICompletionProposal) proposals.get(1);
		assertEquals("foo.bar", proposal.getDisplayString());
		
		assertEquals("Image", WikiPlugin.getDefault().getImageRegistry().get(WikiConstants.PACKAGE_ICON), proposal.getImage());
	}

	public void testGetPackageProposalWithPartitialPackageName() throws Exception {
		ArrayList proposals = prepare(JavaCompletionProcessorTest.TYPE_CONTENTS, JavaCompletionProcessorTest.TYPE_PATH, "foo.ba", 6);
		assertEquals(1, proposals.size());
		ICompletionProposal proposal = (ICompletionProposal) proposals.get(0);
		assertEquals("foo.bar", proposal.getDisplayString());
		assertEquals("Image", WikiPlugin.getDefault().getImageRegistry().get(WikiConstants.PACKAGE_ICON), proposal.getImage());
	}

	public void testGetTypeProposalInPackage() throws Exception {
		ArrayList proposals = prepare(JavaCompletionProcessorTest.TYPE_CONTENTS, JavaCompletionProcessorTest.TYPE_PATH, "foo.bar.", 8);
		assertEquals(1, proposals.size());
		ICompletionProposal proposal = (ICompletionProposal) proposals.get(0);
		assertEquals("Test - foo.bar", proposal.getDisplayString());
	}

	public void testGetTypeProposalInSentence() throws Exception {
		ArrayList proposals = prepare(JavaCompletionProcessorTest.TYPE_CONTENTS, JavaCompletionProcessorTest.TYPE_PATH, "a test foo.bar. thing", 15);
		assertEquals(1, proposals.size());
		ICompletionProposal proposal = (ICompletionProposal) proposals.get(0);
		assertEquals("Test - foo.bar", proposal.getDisplayString());
	}

	public void testGetTypePrefixedWithNonJavaCharacters() throws Exception {
		ArrayList proposals = prepare(JavaCompletionProcessorTest.TYPE_CONTENTS, JavaCompletionProcessorTest.TYPE_PATH, "'''T", 4);
		assertEquals(1, proposals.size());
		ICompletionProposal proposal = (ICompletionProposal) proposals.get(0);
		assertEquals("Test - foo.bar", proposal.getDisplayString());
	}

	public void testGetTypePrefixedWithFullStop() throws Exception {
		ArrayList proposals = prepare(JavaCompletionProcessorTest.TYPE_CONTENTS, JavaCompletionProcessorTest.TYPE_PATH, ".T", 2);
		assertEquals(1, proposals.size());
		ICompletionProposal proposal = (ICompletionProposal) proposals.get(0);
		assertEquals("Test - foo.bar", proposal.getDisplayString());
	}

	public void testGetTypePrefixedWithSpace() throws Exception {
		ArrayList proposals = prepare(JavaCompletionProcessorTest.TYPE_CONTENTS, JavaCompletionProcessorTest.TYPE_PATH, " T", 2);
		assertEquals(1, proposals.size());
		ICompletionProposal proposal = (ICompletionProposal) proposals.get(0);
		assertEquals("Test - foo.bar", proposal.getDisplayString());
	}

	public void testTypeInPackage() throws Exception {
		ArrayList proposals = prepare(JavaCompletionProcessorTest.TYPE_CONTENTS, JavaCompletionProcessorTest.TYPE_PATH, "a test Test thing", 11);
		assertEquals(1, proposals.size());
		ICompletionProposal proposal = (ICompletionProposal) proposals.get(0);
		assertEquals("Test - foo.bar", proposal.getDisplayString());
	}

//	public void testFindsTypesInLibraries() throws Exception {
//		IJavaProject javaProject = JavaCore.create(project);
//		IClasspathEntry defaultJREContainerEntry = JavaRuntime.getDefaultJREContainerEntry();
//		IClasspathEntry[] entries = new IClasspathEntry[] {defaultJREContainerEntry};
//		javaProject.setRawClasspath(entries, null);
//		
//		ArrayList proposals = prepare(JavaCompletionProcessorTest.TYPE_CONTENTS, JavaCompletionProcessorTest.TYPE_PATH, "a test String thing", 13);
//		assertTrue(proposals.size() > 0);
//		boolean found = false;
//		for (Iterator iterator = proposals.iterator(); iterator.hasNext();) {
//			ICompletionProposal proposal = (ICompletionProposal) iterator.next();
//			if ("String - java.lang".equals(proposal.getDisplayString())) {
//				found = true;
//				break;
//			}
//		}
//		assertTrue("Found java.lang.String", found);
//	}

	public void testCursorOnSpace() throws Exception {
		ArrayList proposals = prepare(JavaCompletionProcessorTest.TYPE_CONTENTS, JavaCompletionProcessorTest.TYPE_PATH, "a Test", 1);
		assertEquals(0, proposals.size());
	}

	private ArrayList prepare(String code, String path, String wikiContents, int cursorPosition) throws Exception, PartInitException {
		createAndOpen(code, path);
		WikiEditor editor = createWikiDocumentAndOpen(wikiContents).getEditor();
		ArrayList proposals = new JavaCompletionProcessor().getProposals(getJavaProject(), editor.getTextViewerForTest(), cursorPosition);
		return proposals;
	}

	public void testIsCandidateWhenWhiteSpace() throws Exception {
		ArrayList proposals = prepare(JavaCompletionProcessorTest.TYPE_CONTENTS, JavaCompletionProcessorTest.TYPE_PATH, " ", 1);
		assertEquals(0, proposals.size());
	}

	public void testGetTypeProposalsWhenDotPreceededByWhiteSpace() throws Exception {
		ArrayList proposals = prepare(JavaCompletionProcessorTest.TYPE_CONTENTS, JavaCompletionProcessorTest.TYPE_PATH, " .", 2);
		assertEquals(0, proposals.size());
	}
}
