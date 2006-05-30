package com.teaminabox.eclipse.wiki.text;

import java.io.File;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.editors.WikiDocumentContext;

/**
 * I match links to eclipse resources.
 * <P>
 * The resource must exist for there to be a match
 */
public final class PluginResourceMatcher extends ResourceMatcher {
	public PluginResourceMatcher() {
		super(WikiConstants.PLUGIN_PREFIX);
	}

	public TextRegion createTextRegion(String text, WikiDocumentContext context) {
		if (accepts(text, context)) {
			return new PluginResourceTextRegion(new String(text.substring(0, matchLength(text, context))));
		}
		return null;
	}

	protected File findResourceFromPath(String section) {
		IResource res = PluginResourceTextRegion.findResource(section);
		if (res == null) {
			IPath resPath = PluginResourceTextRegion.getPluginPath(section);
			if (resPath != null) {
				File resFile = resPath.toFile();
				return resFile;
			}
			return null;
		}
		return res.getLocation().toFile();
	}
}