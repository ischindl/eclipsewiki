package com.teaminabox.eclipse.wiki.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.teaminabox.eclipse.wiki.editors.WikiDocumentContext;

/**
 * I match a region of text using a regex.
 */
public abstract class PatternMatcher extends AbstractTextRegionMatcher {

	private final Pattern	pattern;

	public PatternMatcher(String pattern) {
		this.pattern = Pattern.compile(pattern);;
	}

	public final TextRegion createTextRegion(String text, WikiDocumentContext context) {
		int matchLength = matchLength(text);
		if (matchLength > 0) {
			return createTextRegion(text.substring(0, matchLength));
		}
		return null;
	}

	protected abstract TextRegion createTextRegion(String string);

	private int matchLength(String text) {
		Matcher m = pattern.matcher(text);
		if (m.find() && m.start() == 0) {
			return m.end();
		}
		return -1;
	}

}