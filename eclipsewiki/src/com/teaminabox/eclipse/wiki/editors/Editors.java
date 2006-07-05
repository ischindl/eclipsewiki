package com.teaminabox.eclipse.wiki.editors;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import com.teaminabox.eclipse.wiki.WikiPlugin;

public final class Editors {

	private static Set	editors	= Collections.synchronizedSet(new HashSet());

	static {
		addResourceChangeListener();
	}

	private Editors() {
	}

	private static void addResourceChangeListener() {
		IResourceChangeListener resourceChangeListener = new IResourceChangeListener() {
			public void resourceChanged(IResourceChangeEvent event) {
				processResourceChange(event);
			}
		};
		ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceChangeListener);
	}

	private static void processResourceChange(IResourceChangeEvent event) {
		try {
			if (event.getType() != IResourceChangeEvent.POST_CHANGE) {
				return;
			}
			refreshEditors();
		} catch (Exception e) {
			WikiPlugin.getDefault().log("WikiEditor: Resource Change Error", e);
		}
	}

	private static void refreshEditors() throws IOException, CoreException {
		Iterator iterator = editors.iterator();
		while (iterator.hasNext()) {
			WikiEditor editor = (WikiEditor) iterator.next();
			if (editor.getContext() != null) {
				editor.getContext().loadEnvironment();
				editor.redrawTextAsync();
			}
		}
	}

	public static void registerEditor(WikiEditor editor) {
		editors.add(editor);
	}

	public static void unregisterEditor(WikiEditor editor) {
		editors.remove(editor);
	}

	public static WikiEditor[] getEditors() {
		return (WikiEditor[]) editors.toArray(new WikiEditor[editors.size()]);
	}
}
