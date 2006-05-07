package com.teaminabox.eclipse.wiki.util;

import org.eclipse.jface.text.rules.IWhitespaceDetector;

public final class WikiWhitespaceDetector implements IWhitespaceDetector {

	public boolean isWhitespace(char c) {
		return WikiWhitespaceDetector.isWhiteSpaceCharacter(c);
	}

	public static boolean isWhiteSpaceCharacter(char c) {
		return c == ' ' || c == '\t' || c == '\n' || c == '\r';
	}

	public static boolean containsWhiteSpace(String text) {
		for (int i = 0; i < text.length(); i++) {
			if (WikiWhitespaceDetector.isWhiteSpaceCharacter(text.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	public static int indexOfWhiteSpace(String text) {
		if (text.length() == 0) {
			return -1;
		}
		for (int i = 0; i < text.length(); i++) {
			if (Character.isWhitespace(text.charAt(i))) {
				return i;
			}
		}
		return -1;
	}
}