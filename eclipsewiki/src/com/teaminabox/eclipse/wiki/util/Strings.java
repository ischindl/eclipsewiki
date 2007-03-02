package com.teaminabox.eclipse.wiki.util;

public final class Strings {

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

	public static boolean isWhiteSpaceCharacter(char c) {
		return c == ' ' || c == '\t' || c == '\n' || c == '\r';
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
