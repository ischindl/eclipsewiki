package com.teaminabox.eclipse.wiki.text;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.editors.WikiDocumentContext;

/**
 * I match URLs. A match occurs when text starts with a {@link WikiConstants#URL_PREFIXES url prefix}and ends with
 * white space.
 * 
 * @see WikiConstants#URL_PREFIXES
 */
public final class UrlMatcher extends WhiteSpaceTerminatedMatcher {

	private boolean accepts(String text, WikiDocumentContext context) {
		for (int i = 0; i < WikiConstants.URL_PREFIXES.length; i++) {
			if (text.startsWith(WikiConstants.URL_PREFIXES[i])) {
				return true;
			}
		}
		return false;
	}

	public TextRegion createTextRegion(String text, WikiDocumentContext context) {
		if (accepts(text, context)) {
			return new UrlTextRegion(text.substring(0, matchLength(text, context)));
		}
		return null;
	}

	protected boolean accepts(char c, boolean firstCharacter) {
		if (firstCharacter) {
			for (int i = 0; i < WikiConstants.URL_PREFIXES.length; i++) {
				if (c == WikiConstants.URL_PREFIXES[i].charAt(0)) {
					return true;
				}
			}
		}
		return c != ' ';
	}

}