package com.teaminabox.eclipse.wiki.editors;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeMap;

import org.eclipse.jdt.core.CompletionProposal;
import org.eclipse.jdt.core.CompletionRequestor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jdt.ui.text.java.CompletionProposalLabelProvider;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Image;

import com.teaminabox.eclipse.wiki.WikiPlugin;
import com.teaminabox.eclipse.wiki.util.JavaUtils;

/**
 * This is a replacement for the JavaCompletionProcessor.
 * It will replace the JavaCompletionProcessor when I've figured out how to make it search in source too.
 */
public class JavaCompletionProcessor2 {
	
	private static final DecimalFormat	SORT_FORMAT	= new DecimalFormat("0000000000");
	private String						textToComplete;
	private TreeMap						proposals	= new TreeMap(new Comparator() {

														public int compare(Object arg0, Object arg1) {
															String first = (String) arg0;
															String second = (String) arg1;
															return second.compareTo(first);
														}

													});

	public ArrayList getProposals(IJavaProject project, ITextViewer viewer, final int documentOffset) throws BadLocationException {
		proposals.clear();
		textToComplete = getTextToComplete(viewer, documentOffset);

		try {
			if (textToComplete != null) {
				CompletionRequestor collector = getCompletionRequestor(documentOffset, textToComplete);
				performCodeComplete(project, textToComplete, collector);
				ArrayList results = new ArrayList(proposals.values());
				proposals.clear();
				return results;
			}
		} catch (Exception e) {
			WikiPlugin.getDefault().log("Completions", e);
		}
		return new ArrayList();
	}

	private void performCodeComplete(IJavaProject project, final String text, CompletionRequestor collector) throws JavaModelException {
		String code = "public class Foo { " + text + "}";
		ICompilationUnit unit = createCompilationUnit(project, code);
		unit.codeComplete(code.indexOf(text) + text.length(), collector);
		unit.discardWorkingCopy();
	}

	private ICompilationUnit createCompilationUnit(IJavaProject project, String code) throws JavaModelException {
		IClasspathEntry[] classpath = project.getRawClasspath();
		WorkingCopyOwner owner = new WorkingCopyOwner() {
		};
		ICompilationUnit unit = owner.newWorkingCopy("Foo", classpath, new NullProblemRequestor(), null);
		unit.getBuffer().append(code);
		return unit;
	}

	/**
	 * Get the (start of) the fully qualified type at <code>documentOffset</code> that the user is trying to complete.
	 * 
	 * @return the beginnings of the fully qualified type or <code>null</code> if there is nothing.
	 */
	private String getTextToComplete(ITextViewer viewer, int documentOffset) throws BadLocationException {
		IDocument document = viewer.getDocument();
		int characterIndex = documentOffset - 1;
		if (characterIndex < 0 || !JavaUtils.isJavaClassNamePart(document.getChar(characterIndex))) {
			return null;
		}
		int start = characterIndex;
		while (start > 0 && JavaUtils.isJavaClassNamePart(document.getChar(start))) {
			start--;
		}
		while (start < characterIndex && !Character.isJavaIdentifierPart(document.getChar(start))) {
			start++;
		}
		String prefix = document.get(start, characterIndex - start + 1);
		if (".".equals(prefix)) {
			return null;
		}
		return prefix;
	}

	private CompletionRequestor getCompletionRequestor(final int documentOffset, final String text) {
		CompletionRequestor requestor = new CompletionRequestor() {
			public void accept(CompletionProposal proposal) {
				if (proposal.getKind() != CompletionProposal.TYPE_REF && proposal.getKind() != CompletionProposal.PACKAGE_REF) {
					return;
				}
				CompletionProposalLabelProvider labelProvider = new CompletionProposalLabelProvider();
				String matchName = getReplacementText(proposal);
				String displayText = labelProvider.createLabel(proposal);
				Image image = labelProvider.createImageDescriptor(proposal).createImage();
				ICompletionProposal myProposal = new org.eclipse.jface.text.contentassist.CompletionProposal(matchName, documentOffset - text.length(), text.length(), matchName.length(), image, displayText, null, null);
				proposals.put(SORT_FORMAT.format(proposal.getRelevance()) + displayText, myProposal);
			}
		};
		return requestor;
	}

	private String getReplacementText(CompletionProposal proposal) {
		String matchName = new String(proposal.getCompletion());
		String pack = new String(proposal.getDeclarationSignature());
		if (!matchName.startsWith(pack)) {
			// java.lang Classes don't have their package so ...
			matchName = pack + "." + matchName;
		}
		return matchName;
	}
}
