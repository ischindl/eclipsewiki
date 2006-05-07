package com.teaminabox.eclipse.wiki.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.teaminabox.eclipse.wiki.editors.WikiDocumentContext;

/**
 * I match standard WikiNames.
 */
public final class BasicWikiNameMatcher extends AbstractTextRegionMatcher {

	/**
	 * The pattern to match wiki names: <code>([A-Z][a-z]+){2,}[0-9]*</code>
	 */
	private static final Pattern	WIKI_PATTERN	= Pattern.compile("([A-Z][a-z]+){2,}[0-9]*");

	public TextRegion createTextRegion(String text, WikiDocumentContext context) {
		int matchLength = matchLength(text);
		if (matchLength > 0) {
			return new WikiNameTextRegion(text.substring(0, matchLength));
		}
		return null;
	}

	private int matchLength(String text) {
		Matcher m = BasicWikiNameMatcher.WIKI_PATTERN.matcher(text);
		if (m.find() && m.start() == 0) {
			return m.end();
		}
		return -1;
	}

	protected boolean accepts(char c, boolean firstCharacter) {
		if (firstCharacter) {
			return Character.isUpperCase(c);
		}
		return Character.isLetterOrDigit(c);
	}

}