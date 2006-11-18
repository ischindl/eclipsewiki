/**
 * 
 */
package com.teaminabox.eclipse.wiki.editors.completion;

import java.util.Set;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.search.PackageDeclarationMatch;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchRequestor;

public class PackageSearchRequestor extends SearchRequestor {

	private final Set<IJavaElement>	fragments;

	public PackageSearchRequestor(Set<IJavaElement> fragments) {
		this.fragments = fragments;
	}

	public void acceptSearchMatch(SearchMatch match) {
		if (match instanceof PackageDeclarationMatch) {
			PackageDeclarationMatch tdm = (PackageDeclarationMatch) match;
			fragments.add((IJavaElement) tdm.getElement());
		}
	}
}