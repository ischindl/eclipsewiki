package com.teaminabox.eclipse.wiki.text;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.rules.IToken;
import org.osgi.framework.Bundle;

import com.teaminabox.eclipse.wiki.WikiConstants;

/**
 * A region of text referring to a resource in the Eclipse workspace.
 */
public final class PluginResourceTextRegion extends TextRegion {
	public static IPath getPluginPath(String text) {
		if (text == null || text.length() == 0) {
			return null;
		}
		IPath relPath = new Path(text);
		IProject wsProj = PluginProjectSupport.locateProjectInWorkspace(relPath.segment(0));
		if (wsProj == null) {
			Bundle bundle = null;
			try {
				bundle = Platform.getBundle(relPath.segment(0));
			} catch (Exception ex) {
			} finally {
				relPath = relPath.removeFirstSegments(1);
			}
			if (bundle != null) {
				try {
					URL entry = bundle.getEntry(relPath.toString());
					if (entry != null) {
						URL url = FileLocator.toFileURL(entry);
						if (url != null) {
							return new Path(new String(url.getFile().substring(1)));
						}
					}
				} catch (IOException e) {
				}
			}
			return null;
		}
		relPath = relPath.removeFirstSegments(1);
		IResource res = relPath.segmentCount() > 0 ? wsProj.findMember(relPath) : wsProj;
		if (res instanceof IProject) {
			return res.getLocation().addTrailingSeparator();
		} else if (res instanceof IFolder) {
			return res.getLocation().addTrailingSeparator();
		} else if (res != null) {
			return res.getLocation();
		} else {
			return null;
		}
	}

	public static IResource findResource(String path) {
		IPath pluginPath = PluginResourceTextRegion.getPluginPath(path);
		if (pluginPath != null) {
			IResource member = ResourcesPlugin.getWorkspace().getRoot().findMember(pluginPath);
			return member;
		}
		return null;
	}

	// -----------------------------------------------------------------------------

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

	public boolean resourceExists() {
		IResource resource = getResource();
		return resource != null && resource.exists();
	}

	public IResource getResource() {
		return PluginResourceTextRegion.findResource(new String(getText().substring(WikiConstants.PLUGIN_PREFIX.length())));

	}

}