package com.teaminabox.eclipse.wiki.renderer;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.WikiPlugin;

public final class RendererFactory {

	private RendererFactory() {
		super();
	}

	public static ContentRenderer createContentRenderer() {
		try {
			return (ContentRenderer) Class.forName(RendererFactory.getContentRendererName()).newInstance();
		} catch (Exception e) {
			WikiPlugin.getDefault().log("Unable to create renderer.", e);
			return null;
		}
	}

	public static String getContentRendererName() {
		return WikiPlugin.getDefault().getPreferenceStore().getString(WikiConstants.BROWSER_RENDERER);
	}

}
