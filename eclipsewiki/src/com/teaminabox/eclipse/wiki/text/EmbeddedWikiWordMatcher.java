package com.teaminabox.eclipse.wiki.text;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.editors.WikiDocumentContext;

public class EmbeddedWikiWordMatcher extends AbstractTextRegionMatcher {

	private final WikiWordMatcher	wikiWordMatcher;

	public EmbeddedWikiWordMatcher(WikiWordMatcher wikiWordMatcher) {
		this.wikiWordMatcher = wikiWordMatcher;
	}

	protected boolean accepts(char c, boolean firstCharacter) {
		return firstCharacter ? WikiConstants.EMBEDDED_PREFIX.charAt(0) == c : true;
	}

	public TextRegion createTextRegion(String text, WikiDocumentContext context) {
		if (text.startsWith(WikiConstants.EMBEDDED_PREFIX) && text.length() > WikiConstants.EMBEDDED_PREFIX.length() + 1) {
			String embeddedText = text.substring(WikiConstants.EMBEDDED_PREFIX.length());
			TextRegion embedded = wikiWordMatcher.createTextRegion(embeddedText, context);
			if (embedded != null) {
				return new EmbeddedWikiWordTextRegion(WikiConstants.EMBEDDED_PREFIX + embedded.getText(), embedded);
			}
		}
		return null;
	}

}
