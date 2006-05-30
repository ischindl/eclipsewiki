package com.teaminabox.eclipse.wiki.editors;

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
import org.eclipse.jdt.core.search.PackageDeclarationMatch;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.core.search.TypeDeclarationMatch;
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

	public static final Comparator	IJAVA_ELEMENT_COMPARATOR	= new Comparator() {
																	public int compare(Object o1, Object o2) {
																		IJavaElement first = (IJavaElement) o1;
																		IJavaElement second = (IJavaElement) o2;
																		int typeName = first.getElementName().compareTo(second.getElementName());
																		if (typeName != 0) {
																			return typeName;
																		}
																		return first.getElementName().compareTo(second.getElementName());
																	}
																};

	private IJavaProject			project;
	private ArrayList				proposals;

	private boolean					includePackages;

	public JavaCompletionProcessor() {
		proposals = new ArrayList();
	}

	public ArrayList getProposals(IJavaProject project, ITextViewer viewer, int documentOffset) throws BadLocationException {
		includePackages = true;
		proposals.clear();

		this.project = project;
		String text = getFullyQualifiedTypePrefix(viewer, documentOffset);
		if (text == null) {
			return proposals;
		}
		try {
			IPackageFragment[] packages = getMatchingPackageFragments(text);
			IType[] types = getMatchingTypes(packages, text);
			buildProposals(types, text, documentOffset);
			if (includePackages) {
				buildProposals(packages, text, documentOffset);
			}
		} catch (CoreException e) {
			WikiPlugin.getDefault().logAndReport("Completion Error", e.getLocalizedMessage(), e);
		}
		return proposals;
	}

	private void buildProposals(IType[] types, String text, int documentOffset) throws JavaModelException {
		TreeMap sortedProposals = new TreeMap(String.CASE_INSENSITIVE_ORDER);
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
		TreeMap sortedProposals = new TreeMap(String.CASE_INSENSITIVE_ORDER);
		for (int i = 0; i < packages.length; i++) {
			String matchName = packages[i].getElementName();
			ICompletionProposal proposal = new CompletionProposal(matchName, documentOffset - text.length(), text.length(), matchName.length(), WikiPlugin.getDefault().getImageRegistry().get(WikiConstants.PACKAGE_ICON), null, null, null);
			sortedProposals.put(matchName, proposal);
		}
		proposals.addAll(sortedProposals.values());
	}

	private IType[] getMatchingTypes(IPackageFragment[] fragments, String text) throws CoreException {
		final Set matches = new TreeSet(JavaCompletionProcessor.IJAVA_ELEMENT_COMPARATOR);
		SearchRequestor requestor = new SearchRequestor() {
			public void acceptSearchMatch(SearchMatch match) {
				if (match instanceof TypeDeclarationMatch) {
					TypeDeclarationMatch tdm = (TypeDeclarationMatch) match;
					matches.add(tdm.getElement());
				}
			}
		};
		IJavaElement[] elements;
		if (fragments.length == 0) {
			elements = new IJavaElement[] { project };
		} else {
			elements = fragments;
		}
		String prefix = text;
		if (text.indexOf('.') > 0) {
			prefix = new String(text.substring(text.lastIndexOf('.') + 1));
		}
		if (prefix.length() == 0) {
			return getTypesInPackages(fragments);
		}
		IJavaSearchScope scope = SearchEngine.createJavaSearchScope(elements, IJavaSearchScope.SOURCES);
		SearchEngine searchEngine = new SearchEngine();
		SearchPattern pattern = SearchPattern.createPattern(prefix, IJavaSearchConstants.TYPE, IJavaSearchConstants.DECLARATIONS, SearchPattern.R_PREFIX_MATCH);
		searchEngine.search(pattern, new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() }, scope, requestor, null);
		return (IType[]) matches.toArray(new IType[matches.size()]);
	}

	private IType[] getTypesInPackages(IPackageFragment[] fragments) throws JavaModelException {
		includePackages = false;
		HashSet types = new HashSet();
		for (int i = 0; i < fragments.length; i++) {
			addTypesInPackage(fragments[i], types);
		}
		return (IType[]) types.toArray(new IType[types.size()]);
	}

	private void addTypesInPackage(IPackageFragment fragment, HashSet types) throws JavaModelException {
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
		final Set fragments = new TreeSet(JavaCompletionProcessor.IJAVA_ELEMENT_COMPARATOR);
		SearchRequestor requestor = new SearchRequestor() {
			public void acceptSearchMatch(SearchMatch match) {
				if (match instanceof PackageDeclarationMatch) {
					PackageDeclarationMatch tdm = (PackageDeclarationMatch) match;
					fragments.add(tdm.getElement());
				}
			}
		};
		IJavaSearchScope scope = SearchEngine.createJavaSearchScope(new IJavaElement[] { project }, IJavaSearchScope.SOURCES);
		SearchEngine searchEngine = new SearchEngine();
		SearchPattern pattern = SearchPattern.createPattern(prefix, IJavaSearchConstants.PACKAGE, IJavaSearchConstants.DECLARATIONS, SearchPattern.R_PREFIX_MATCH);
		searchEngine.search(pattern, new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() }, scope, requestor, null);
		return (IPackageFragment[]) fragments.toArray(new IPackageFragment[fragments.size()]);
	}

	/**
	 * Get the (start of) the fully qualified type at <code>documentOffset</code> that the user is trying to complete.
	 * 
	 * @return the beginnings of the fully qualified type or <code>null</code> if there is nothing.
	 */
	private String getFullyQualifiedTypePrefix(ITextViewer viewer, int documentOffset) throws BadLocationException {
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

}