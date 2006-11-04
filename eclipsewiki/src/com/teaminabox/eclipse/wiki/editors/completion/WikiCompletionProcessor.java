package com.teaminabox.eclipse.wiki.editors.completion;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

import com.teaminabox.eclipse.wiki.WikiPlugin;
import com.teaminabox.eclipse.wiki.editors.WikiEditor;

public final class WikiCompletionProcessor implements IContentAssistProcessor {

	private static final char[]					AUTO_ACTIVATION_CHARACTERS	= new char[] { '.', '/' };
	private static final ICompletionProposal[]	EMPTY_COMPLETIONS			= new ICompletionProposal[0];

	private WikiEditor							wikiEditor;
	private JavaCompletionProcessor				javaCompletionProcessor;
	private ResourceCompletionProcessor			resourceCompletions;

	public WikiCompletionProcessor(WikiEditor wikiEditor) {
		this.wikiEditor = wikiEditor;
		javaCompletionProcessor = new JavaCompletionProcessor();
		resourceCompletions = new ResourceCompletionProcessor(wikiEditor);
	}

	public ICompletionProposal[] computeCompletionProposals(final ITextViewer viewer, int documentOffset) {
		try {
			ArrayList proposals = new ArrayList();
			IProject project = wikiEditor.getContext().getWorkingLocation().getProject();

			boolean tryJava = resourceCompletions.addCompletions(viewer, documentOffset, proposals);

			if (tryJava && project.hasNature(JavaCore.NATURE_ID)) {
				IJavaProject javaProject = JavaCore.create(project);
				proposals.addAll(javaCompletionProcessor.getProposals(javaProject, viewer, documentOffset));
			}
			return (ICompletionProposal[]) proposals.toArray(new ICompletionProposal[proposals.size()]);
		} catch (Exception e) {
			WikiPlugin.getDefault().logAndReport("Completion Processor", e.getLocalizedMessage(), e);
			return WikiCompletionProcessor.EMPTY_COMPLETIONS;
		}
	}

	public IContextInformation[] computeContextInformation(ITextViewer viewer, int documentOffset) {
		return new IContextInformation[0];
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getCompletionProposalAutoActivationCharacters()
	 */
	public char[] getCompletionProposalAutoActivationCharacters() {
		return WikiCompletionProcessor.AUTO_ACTIVATION_CHARACTERS;
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationAutoActivationCharacters()
	 */
	public char[] getContextInformationAutoActivationCharacters() {
		return new char[0];
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getErrorMessage()
	 */
	public String getErrorMessage() {
		return null;
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationValidator()
	 */
	public IContextInformationValidator getContextInformationValidator() {
		return NullValidator.INSTANCE;
	}

}