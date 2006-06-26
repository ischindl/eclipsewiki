/**
 * 
 */
package com.teaminabox.eclipse.wiki.editors.completion;

import java.util.Set;

import org.eclipse.jdt.core.search.PackageDeclarationMatch;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchRequestor;

public class PackageSearchRequestor extends SearchRequestor {
	
	private final Set	fragments;

	public PackageSearchRequestor(Set fragments) {
		this.fragments = fragments;
	}

	public void acceptSearchMatch(SearchMatch match) {
		if (match instanceof PackageDeclarationMatch) {
			PackageDeclarationMatch tdm = (PackageDeclarationMatch) match;
			fragments.add(tdm.getElement());
		}
	}
}