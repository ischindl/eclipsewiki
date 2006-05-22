package com.teaminabox.eclipse.wiki.text;


/**
 * I match a region of text representing an escaped Wiki word based on a regex.
 */
public final class EscapedWikiWordMatcher extends PatternMatcher {

	private final char	firstCharacter;

	public EscapedWikiWordMatcher(String pattern, char firstCharacter) {
		super(pattern);
		this.firstCharacter = firstCharacter;
	}

	protected TextRegion createTextRegion(String text) {
		return new BasicTextRegion(text);
	}

	protected boolean accepts(char c, boolean isFirstCharacter) {
		if (isFirstCharacter) {
			return c == firstCharacter;
		}
		return Character.isLetterOrDigit(c);
	}

}