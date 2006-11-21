package com.teaminabox.eclipse.wiki.text;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.rules.IToken;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.editors.ColourManager;

/**
 * A region of text that is a Wiki name.
 */
public abstract class WikiLinkTextRegion extends TextRegion {

	public WikiLinkTextRegion(String text) {
		super(text);
	}

	public final IToken getToken(ColourManager colourManager) {
		if (!colourManager.getWikiEditor().isLocal()) {
			return getToken(WikiConstants.WIKI_NAME, colourManager);
		}
		IFile file = colourManager.getWikiEditor().getContext().getFileForWikiName(getWikiDocumentName());
		if (file != null && file.exists()) {
			return getToken(WikiConstants.WIKI_NAME, colourManager);
		}
		return getToken(WikiConstants.NEW_WIKI_NAME, colourManager);
	}

	public abstract String getWikiDocumentName();

	/**
	 * This is a special Wiki region of text.
	 * 
	 * @return <code>true</code>
	 */
	public final boolean isLink() {
		return true;
	}

	/**
	 * Put spaces before the capital of a wiki word (except the first character).
	 */
	public static String deCamelCase(String wikiWord) {
		StringBuffer buffer = new StringBuffer(wikiWord.length() * 2);
		buffer.append(wikiWord.charAt(0));
		for (int i = 1; i < wikiWord.length(); i++) {
			if (!Character.isWhitespace(wikiWord.charAt(i - 1)) && Character.isUpperCase(wikiWord.charAt(i))) {
				buffer.append(' ');
			}
			buffer.append(wikiWord.charAt(i));
		}
		return buffer.toString();
	}

}