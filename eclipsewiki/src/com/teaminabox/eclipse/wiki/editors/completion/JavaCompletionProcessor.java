package com.teaminabox.eclipse.wiki.editors.completion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Image;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.WikiPlugin;
import com.teaminabox.eclipse.wiki.util.JavaUtils;

public final class JavaCompletionProcessor {

	public static final Comparator<IJavaElement>	JAVA_ELEMENT_COMPARATOR	= new JavaElementComparator();

	private IJavaProject							project;
	private ArrayList<ICompletionProposal>			proposals;

	private boolean									includePackages;

	public JavaCompletionProcessor() {
		proposals = new ArrayList<ICompletionProposal>();
	}

	public ArrayList<ICompletionProposal> getProposals(IJavaProject project, ITextViewer viewer, int documentOffset) throws BadLocationException, CoreException {
		String text = initialise(project, viewer, documentOffset);
		if (text == null) {
			return proposals;
		}
		IPackageFragment[] packages = getMatchingPackageFragments(text);
		IType[] types = getMatchingTypes(packages, text);
		buildProposals(types, text, documentOffset);
		if (includePackages) {
			buildProposals(packages, text, documentOffset);
		}
		proposals.addAll(new JavaCompletionProcessor2().getProposals(project, viewer, documentOffset));
		return proposals;
	}

	private String initialise(IJavaProject project, ITextViewer viewer, int documentOffset) throws BadLocationException {
		includePackages = true;
		proposals.clear();
		this.project = project;
		return getFullyQualifiedTypePrefix(viewer, documentOffset);
	}

	private void buildProposals(IType[] types, String text, int documentOffset) throws JavaModelException {
		TreeMap<String, ICompletionProposal> sortedProposals = new TreeMap<String, ICompletionProposal>(String.CASE_INSENSITIVE_ORDER);
		for (int i = 0; i < types.length; i++) {
			String matchName = types[i].getFullyQualifiedName();
			String display = getDisplayString(types[i]);
			ICompletionProposal proposal = new CompletionProposal(matchName, documentOffset - text.length(), text.length(), matchName.length(), getIcon(types[i]), display, null, null);
			sortedProposals.put(matchName, proposal);
		}
		proposals.addAll(sortedProposals.values());
	}

	private Image getIcon(IType type) throws JavaModelException {
		Image icon = type.isInterface() ? WikiPlugin.getDefault().getImageRegistry().get(WikiConstants.INTERFACE_ICON) : WikiPlugin.getDefault().getImageRegistry().get(WikiConstants.CLASS_ICON);
		return icon;
	}

	private String getDisplayString(IType type) {
		String suffix;
		if (type.getPackageFragment().isDefaultPackage()) {
			suffix = " - (default package)";
		} else {
			suffix = " - " + type.getPackageFragment().getElementName();
		}
		String display = type.getElementName() + suffix;
		return display;
	}

	private void buildProposals(IPackageFragment[] packages, String text, int documentOffset) {
		TreeMap<String, ICompletionProposal> sortedProposals = new TreeMap<String, ICompletionProposal>(String.CASE_INSENSITIVE_ORDER);
		for (int i = 0; i < packages.length; i++) {
			String matchName = packages[i].getElementName();
			ICompletionProposal proposal = new CompletionProposal(matchName, documentOffset - text.length(), text.length(), matchName.length(), WikiPlugin.getDefault().getImageRegistry().get(WikiConstants.PACKAGE_ICON), null, null, null);
			sortedProposals.put(matchName, proposal);
		}
		proposals.addAll(sortedProposals.values());
	}

	private IType[] getMatchingTypes(IPackageFragment[] fragments, String text) throws CoreException {
		final Set<IJavaElement> matches = new TreeSet<IJavaElement>(JavaCompletionProcessor.JAVA_ELEMENT_COMPARATOR);
		IJavaElement[] elements = getJavaElementsToSearchForTypes(fragments);
		String prefix = getPackagePrefix(text);
		if (prefix.length() == 0) {
			return getTypesInPackages(fragments);
		}
		IJavaSearchScope scope = SearchEngine.createJavaSearchScope(elements, IJavaSearchScope.SOURCES);
		SearchPattern pattern = SearchPattern.createPattern(prefix, IJavaSearchConstants.TYPE, IJavaSearchConstants.DECLARATIONS, SearchPattern.R_PREFIX_MATCH);
		new SearchEngine().search(pattern, new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() }, scope, new TypeSearchRequestor(matches), null);
		return (IType[]) matches.toArray(new IType[matches.size()]);
	}

	private String getPackagePrefix(String fullyQualifiedTypeName) {
		if (fullyQualifiedTypeName.indexOf('.') > 0) {
			return new String(fullyQualifiedTypeName.substring(fullyQualifiedTypeName.lastIndexOf('.') + 1));
		}
		return fullyQualifiedTypeName;
	}

	private IJavaElement[] getJavaElementsToSearchForTypes(IPackageFragment[] fragments) {
		IJavaElement[] elements;
		if (fragments.length == 0) {
			elements = new IJavaElement[] { project };
		} else {
			elements = fragments;
		}
		return elements;
	}

	private IType[] getTypesInPackages(IPackageFragment[] fragments) throws JavaModelException {
		includePackages = false;
		HashSet<IType> types = new HashSet<IType>();
		for (int i = 0; i < fragments.length; i++) {
			addTypesInPackage(fragments[i], types);
		}
		return (IType[]) types.toArray(new IType[types.size()]);
	}

	private void addTypesInPackage(IPackageFragment fragment, HashSet<IType> types) throws JavaModelException {
		IJavaElement[] children = fragment.getChildren();
		for (int i = 0; i < children.length; i++) {
			if (children[i].getElementType() == IJavaElement.COMPILATION_UNIT) {
				ICompilationUnit compilationUnit = (ICompilationUnit) children[i];
				types.addAll(Arrays.asList(compilationUnit.getTypes()));
			}
		}
	}

	private IPackageFragment[] getMatchingPackageFragments(final String text) throws CoreException {
		String prefix = text;
		if (text.endsWith(".")) {
			prefix = new String(text.substring(0, text.length() - 1));
		}
		final Set<IJavaElement> fragments = new TreeSet<IJavaElement>(JavaCompletionProcessor.JAVA_ELEMENT_COMPARATOR);
		IJavaSearchScope scope = SearchEngine.createJavaSearchScope(new IJavaElement[] { project });
		SearchPattern pattern = SearchPattern.createPattern(prefix, IJavaSearchConstants.PACKAGE, IJavaSearchConstants.DECLARATIONS, SearchPattern.R_PREFIX_MATCH);
		new SearchEngine().search(pattern, new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() }, scope, new PackageSearchRequestor(fragments), null);
		return (IPackageFragment[]) fragments.toArray(new IPackageFragment[fragments.size()]);
	}

	/**
	 * Get the (start of) the fully qualified type at <code>documentOffset</code> that the user is trying to complete.
	 * 
	 * @return the beginnings of the fully qualified type or <code>null</code> if there is nothing.
	 */
	private String getFullyQualifiedTypePrefix(ITextViewer viewer, int documentOffset) throws BadLocationException {
		IDocument document = viewer.getDocument();
		int cursorIndex = documentOffset - 1;
		if (cursorIndex < 0 || !JavaUtils.isJavaClassNamePart(document.getChar(cursorIndex))) {
			return null;
		}
		int start = findBeginningOfText(document, cursorIndex);
		start = findEndOfText(document, cursorIndex, start);
		String prefix = document.get(start, cursorIndex - start + 1);
		if (".".equals(prefix)) {
			return null;
		}
		return prefix;
	}

	private int findEndOfText(IDocument document, int cursorIndex, int from) throws BadLocationException {
		int end = from;
		while (end < cursorIndex && !Character.isJavaIdentifierPart(document.getChar(end))) {
			end++;
		}
		return end;
	}

	private int findBeginningOfText(IDocument document, int characterIndex) throws BadLocationException {
		int start = characterIndex;
		while (start > 0 && JavaUtils.isJavaClassNamePart(document.getChar(start))) {
			start--;
		}
		return start;
	}

}