package com.teaminabox.eclipse.wiki.editors.completion;

import java.util.Comparator;

import org.eclipse.jdt.core.IJavaElement;

public class JavaElementComparator implements Comparator {
	public int compare(Object o1, Object o2) {
		IJavaElement first = (IJavaElement) o1;
		IJavaElement second = (IJavaElement) o2;
		return first.getHandleIdentifier().compareTo(second.getHandleIdentifier());
	}
}