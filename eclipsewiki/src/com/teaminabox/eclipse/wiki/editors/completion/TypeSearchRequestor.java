package com.teaminabox.eclipse.wiki.editors.completion;

import java.util.Set;

import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.core.search.TypeDeclarationMatch;

public class TypeSearchRequestor extends SearchRequestor {
	private final Set	matches;

	public TypeSearchRequestor(Set matches) {
		this.matches = matches;
	}

	public void acceptSearchMatch(SearchMatch match) {
		if (match instanceof TypeDeclarationMatch) {
			TypeDeclarationMatch tdm = (TypeDeclarationMatch) match;
			matches.add(tdm.getElement());
		}
	}
}