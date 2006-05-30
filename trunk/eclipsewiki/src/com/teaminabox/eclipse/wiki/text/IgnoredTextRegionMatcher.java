package com.teaminabox.eclipse.wiki.text;

import java.util.Iterator;

import com.teaminabox.eclipse.wiki.editors.WikiDocumentContext;

public final class IgnoredTextRegionMatcher extends AbstractTextRegionMatcher {

	private int matchLength(String text, WikiDocumentContext context) {
		Iterator iterator = context.getExcludeSet().iterator();
		while (iterator.hasNext()) {
			String excluded = (String) iterator.next();
			if (text.startsWith(excluded)) {
				return excluded.length();
			}
		}
		return 0;
	}

	public TextRegion createTextRegion(String text, WikiDocumentContext context) {
		int matchLengh = matchLength(text, context);
		if (matchLengh > 0) {
			return new BasicTextRegion(new String(text.substring(0, matchLength(text, context))));
		}
		return null;
	}

	protected boolean accepts(char c, boolean firstCharacter) {
		return c != ' ';
	}

}
