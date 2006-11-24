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
}
