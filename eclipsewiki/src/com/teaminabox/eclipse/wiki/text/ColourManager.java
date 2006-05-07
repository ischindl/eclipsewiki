package com.teaminabox.eclipse.wiki.text;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import com.teaminabox.eclipse.wiki.editors.WikiEditor;

public final class ColourManager {

	private Map			fColorTable	= new HashMap();
	private WikiEditor	editor;

	public ColourManager(WikiEditor editor) {
		this.editor = editor;
	}

	public WikiEditor getWikiEditor() {
		return editor;
	}

	public void dispose() {
		Iterator e = fColorTable.values().iterator();
		while (e.hasNext()) {
			((Color) e.next()).dispose();
		}
	}

	public Color getColor(RGB rgb) {
		Color color = (Color) fColorTable.get(rgb);
		if (color == null) {
			color = new Color(Display.getCurrent(), rgb);
			fColorTable.put(rgb, color);
		}
		return color;
	}
}