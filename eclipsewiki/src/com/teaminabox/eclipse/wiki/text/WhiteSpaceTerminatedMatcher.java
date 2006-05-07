package com.teaminabox.eclipse.wiki.text;

import com.teaminabox.eclipse.wiki.editors.WikiDocumentContext;
import com.teaminabox.eclipse.wiki.util.WikiWhitespaceDetector;

public abstract class WhiteSpaceTerminatedMatcher extends AbstractTextRegionMatcher {

	protected final int matchLength(String text, WikiDocumentContext context) {
		int whitespace = WikiWhitespaceDetector.indexOfWhiteSpace(text);
		if (whitespace > 0) {
			return whitespace;
		}
		return text.length();
	}

}