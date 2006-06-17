package com.teaminabox.eclipse.wiki.util;

public final class JavaUtils {

	public static boolean isJavaClassNamePart(char c) {
		return Character.isJavaIdentifierPart(c) || c == '.';
	}
}