package com.teaminabox.eclipse.wiki.editors.completion;

import java.util.Set;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.core.search.TypeDeclarationMatch;

public class TypeSearchRequestor extends SearchRequestor {
	private final Set<IJavaElement>	matches;

	public TypeSearchRequestor(Set<IJavaElement> matches) {
		this.matches = matches;
	}

	public void acceptSearchMatch(SearchMatch match) {
		if (match instanceof TypeDeclarationMatch) {
			TypeDeclarationMatch tdm = (TypeDeclarationMatch) match;
			matches.add((IJavaElement) tdm.getElement());
		}
	}
}