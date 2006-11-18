package com.teaminabox.eclipse.wiki.editors.completion;

import java.util.Comparator;

import org.eclipse.jdt.core.IJavaElement;

public class JavaElementComparator implements Comparator<IJavaElement> {
	public int compare(IJavaElement o1, IJavaElement o2) {
		return o1.getHandleIdentifier().compareTo(o2.getHandleIdentifier());
	}
}