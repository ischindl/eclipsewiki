package com.teaminabox.eclipse.wiki.export;

import java.util.HashMap;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.JavaModelException;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.WikiPlugin;
import com.teaminabox.eclipse.wiki.renderer.LinkMaker;
import com.teaminabox.eclipse.wiki.text.EclipseResourceTextRegion;
import com.teaminabox.eclipse.wiki.text.JavaTypeTextRegion;
import com.teaminabox.eclipse.wiki.text.PluginResourceTextRegion;
import com.teaminabox.eclipse.wiki.text.WikiLinkTextRegion;
import com.teaminabox.eclipse.wiki.text.WikiUrlTextRegion;
import com.teaminabox.eclipse.wiki.util.Resources;

public final class ExportLinkMaker extends LinkMaker {

	/**
	 * The set of documents linked to from wiki pages to the Eclipse workspace. Key = IResource, Value = link location
	 */
	private HashMap	linkedResources;

	public ExportLinkMaker() {
		linkedResources = new HashMap();
	}

	public String make(WikiLinkTextRegion wikiNameTextRegion) {
		if (getContext().hasWikiSibling(wikiNameTextRegion)) {
			return getLink(wikiNameTextRegion.getWikiDocumentName() + WikiExporter.HTML_EXTENSION, wikiNameTextRegion.getDisplayText());
		}
		return wikiNameTextRegion.getDisplayText();
	}

	public String make(WikiUrlTextRegion wikiUrlTextRegion) {
		if (wikiUrlTextRegion.getLink().startsWith(WikiConstants.ECLIPSE_PREFIX)) {
			return make(new EclipseResourceTextRegion(wikiUrlTextRegion.getLink()));
		} else if (wikiUrlTextRegion.getLink().startsWith(WikiConstants.PLUGIN_PREFIX)) {
			return make(new PluginResourceTextRegion(wikiUrlTextRegion.getLink()));
		}
		return getLink(wikiUrlTextRegion.getLink(), wikiUrlTextRegion.getText());
	}

	public String make(EclipseResourceTextRegion eclipseResourceTextRegion) {
		if (Resources.exists(eclipseResourceTextRegion.getResource()) && eclipseResourceTextRegion.getResource().getType() == IResource.FILE) {
			String href = getHref(eclipseResourceTextRegion);
			String link = getLink(href, eclipseResourceTextRegion.getText());
			linkedResources.put(eclipseResourceTextRegion.getResource(), href);
			return link;
		}
		return eclipseResourceTextRegion.getText();
	}

	public String make(PluginResourceTextRegion pluginResourceTextRegion) {
		if (Resources.exists(pluginResourceTextRegion.getResource()) && pluginResourceTextRegion.getResource().getType() == IResource.FILE) {
			String href = getHref(pluginResourceTextRegion);
			String link = getLink(href, pluginResourceTextRegion.getText());
			linkedResources.put(pluginResourceTextRegion.getResource(), href);
			return link;
		}
		return pluginResourceTextRegion.getText();
	}

	public String make(JavaTypeTextRegion region) {
		try {
			if (region.getType().getUnderlyingResource() != null) {
				String url = getHref(region);
				linkedResources.put(region.getType().getUnderlyingResource(), url);
				return getLink(url, getTextForJavaType(region));
			}
			return region.getText();
		} catch (JavaModelException e) {
			WikiPlugin.getDefault().logAndReport("Error", e.getLocalizedMessage(), e);
			return "Error";
		}
	}

	public String getHref(JavaTypeTextRegion region) throws JavaModelException {
		return WikiExporter.WORKSPACE + region.getType().getUnderlyingResource().getFullPath().toString() + ".html";
	}

	public String getHref(EclipseResourceTextRegion eclipseResourceTextRegion) {
		if (eclipseResourceTextRegion.getResource().getName().endsWith("java")) {
			return WikiExporter.WORKSPACE + eclipseResourceTextRegion.getResource().getFullPath().toString() + ".html";
		}
		return WikiExporter.WORKSPACE + eclipseResourceTextRegion.getResource().getFullPath().toString();
	}

	public String getHref(PluginResourceTextRegion pluginResourceTextRegion) {
		if (pluginResourceTextRegion.getResource().getName().endsWith("java")) {
			return WikiExporter.WORKSPACE + pluginResourceTextRegion.getResource().getFullPath().toString() + ".html";
		}
		return WikiExporter.WORKSPACE + pluginResourceTextRegion.getResource().getFullPath().toString();
	}

	public HashMap getLinkedResources() {
		return linkedResources;
	}

	public boolean hasLinkedDocuments() {
		return linkedResources.size() > 0;
	}

}
