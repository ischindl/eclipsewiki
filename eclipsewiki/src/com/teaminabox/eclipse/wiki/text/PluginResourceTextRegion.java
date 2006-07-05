package com.teaminabox.eclipse.wiki.text;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.rules.IToken;

import com.teaminabox.eclipse.wiki.WikiConstants;

/**
 * A region of text referring to a resource in the Eclipse workspace.
 */
public final class PluginResourceTextRegion extends TextRegion {

	public static IResource findResource(String path) {
		IPath pluginPath = PluginPathFinder.getPluginPath(path);
		if (pluginPath != null) {
			return ResourcesPlugin.getWorkspace().getRoot().findMember(pluginPath);
		}
		return null;
	}

	public PluginResourceTextRegion(String text) {
		super(text);
	}

	/**
	 * @see com.teaminabox.eclipse.wiki.text.TextRegion#accept(com.teaminabox.eclipse.wiki.text.TextRegionVisitor)
	 */
	public Object accept(TextRegionVisitor textRegionVisitor) {
		return textRegionVisitor.visit(this);
	}

	/**
	 * This is a special Wiki region of text.
	 * 
	 * @return <code>true</code>
	 */
	public boolean isLink() {
		return true;
	}

	/**
	 * @see com.teaminabox.eclipse.wiki.text.TextRegion#getToken(ColourManager colourManager)
	 */
	public IToken getToken(ColourManager colourManager) {
		return getToken(WikiConstants.PLUGIN_RESOURCE, colourManager);
	}

	public IResource getResource() {
		String resource = new String(getText().substring(WikiConstants.PLUGIN_PREFIX.length()));
		return PluginResourceTextRegion.findResource(resource);
	}

}