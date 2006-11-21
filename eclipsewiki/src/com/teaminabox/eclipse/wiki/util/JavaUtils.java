package com.teaminabox.eclipse.wiki.util;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaCore;

public final class JavaUtils {

	public static boolean isJavaClassNamePart(char c) {
		return Character.isJavaIdentifierPart(c) || c == '.';
	}
	
	public static boolean isJavaProject(IProject project) throws CoreException {
		return project.hasNature(JavaCore.NATURE_ID);
	}
}