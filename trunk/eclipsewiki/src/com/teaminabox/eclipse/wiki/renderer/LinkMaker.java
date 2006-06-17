package com.teaminabox.eclipse.wiki.renderer;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.WikiPlugin;
import com.teaminabox.eclipse.wiki.editors.WikiDocumentContext;
import com.teaminabox.eclipse.wiki.text.EclipseResourceTextRegion;
import com.teaminabox.eclipse.wiki.text.JavaTypeTextRegion;
import com.teaminabox.eclipse.wiki.text.PluginResourceTextRegion;
import com.teaminabox.eclipse.wiki.text.UrlTextRegion;
import com.teaminabox.eclipse.wiki.text.WikiLinkTextRegion;
import com.teaminabox.eclipse.wiki.text.WikiUrlTextRegion;

public abstract class LinkMaker {

	private WikiDocumentContext	context;

	public abstract String make(WikiLinkTextRegion wikiNameTextRegion);

	public abstract String make(WikiUrlTextRegion wikiUrlTextRegion);

	public abstract String make(EclipseResourceTextRegion eclipseResourceTextRegion);

	public abstract String make(PluginResourceTextRegion pluginResourceTextRegion);

	public abstract String make(JavaTypeTextRegion region);

	public final void setContext(WikiDocumentContext context) {
		this.context = context;
	}

	public final WikiDocumentContext getContext() {
		return context;
	}

	public final String getLink(String url, String text) {
		if (isLinkToImage(url)) {
			String resolved = resolveWikiLink(url);
			if (resolved != null) {
				return "<img alt=\"" + text + "\" src=\"" + resolved + "\"/>";
			}
			return text + " [?]";
		}
		return "<a href=\"" + url + "\">" + text + "</a>";
	}

	/**
	 * Resolve a Wiki Link to an OS dependent url. Wiki Links start with {@link WikiConstants#WIKI_HREF http://--wiki/}
	 * and may also include {@link WikiConstants#ECLIPSE_PREFIX Eclipse:} thus looking like this:
	 * http://--wiki/Eclipse:/project/folder
	 * 
	 * @return the resolved link or null if it cannot be resolved
	 */
	private String resolveWikiLink(String url) {
		if (!url.startsWith(WikiConstants.WIKI_HREF)) {
			return url;
		}

		url = new String(url.substring(WikiConstants.WIKI_HREF.length()));
		if (url.startsWith(WikiConstants.ECLIPSE_PREFIX)) {
			url = new String(url.substring(WikiConstants.ECLIPSE_PREFIX.length()));
		}
		IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(url));
		if (resource == null) {
			return null;
		}
		return "file://" + resource.getLocation().toOSString();
	}

	public String make(UrlTextRegion urlTextRegion) {
		return getLink(urlTextRegion.getText(), urlTextRegion.getText());
	}

	public boolean isLinkToImage(String url) {
		if (url == null) {
			return false;
		}
		return url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(".jpe") || url.endsWith(".gif") || url.endsWith(".png");
	}

	protected String getTextForJavaType(JavaTypeTextRegion region) {
		int lastSeparator = region.getText().lastIndexOf('.');
		if (lastSeparator < 0 || WikiPlugin.getDefault().getPreferenceStore().getBoolean(WikiConstants.RENDER_FULLY_QUALIFIED_TYPE_NAMES)) {
			return region.getText();
		}
		return new String(region.getText().substring(lastSeparator + 1));
	}

}
