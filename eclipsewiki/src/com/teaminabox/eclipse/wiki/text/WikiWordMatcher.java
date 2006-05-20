package com.teaminabox.eclipse.wiki.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.teaminabox.eclipse.wiki.editors.WikiDocumentContext;

/**
 * I match a region of text based on a regex.
 */
public final class WikiWordMatcher extends AbstractTextRegionMatcher {

	private final Pattern	pattern;

	public WikiWordMatcher(String pattern) {
		this.pattern = Pattern.compile(pattern);;
	}

	public TextRegion createTextRegion(String text, WikiDocumentContext context) {
		int matchLength = matchLength(text);
		if (matchLength > 0) {
			return new WikiWordTextRegion(text.substring(0, matchLength));
		}
		return null;
	}

	private int matchLength(String text) {
		Matcher m = pattern.matcher(text);
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