package com.teaminabox.eclipse.wiki.renderer;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.WikiPlugin;
import com.teaminabox.eclipse.wiki.editors.WikiEditor;
import com.teaminabox.eclipse.wiki.text.EclipseResourceTextRegion;
import com.teaminabox.eclipse.wiki.text.GenericTextRegionVisitor;
import com.teaminabox.eclipse.wiki.text.PluginPathFinder;
import com.teaminabox.eclipse.wiki.text.PluginResourceTextRegion;
import com.teaminabox.eclipse.wiki.text.TextRegion;
import com.teaminabox.eclipse.wiki.text.TextRegionBuilder;
import com.teaminabox.eclipse.wiki.text.WikiLinkTextRegion;
import com.teaminabox.eclipse.wiki.text.WikiWordTextRegion;
import com.teaminabox.eclipse.wiki.util.Resources;

public class SelectionRenderer {

	private final WikiEditor	wikiEditor;
	private final ITextViewer	textViewer;
	private final IRegion		region;

	public SelectionRenderer(final WikiEditor wikiEditor, final ITextViewer textViewer, final IRegion region) {
		this.wikiEditor = wikiEditor;
		this.textViewer = textViewer;
		this.region = region;

	}

	public String render() {
		if (!wikiEditor.isLocal()) {
			return null;
		}

		TextRegion textRegion = TextRegionBuilder.getTextRegionAtCursor(wikiEditor, textViewer.getDocument(), region.getOffset());
		String info = getHoverInfo(textRegion);
		// the cursor may be placed between two text regions
		// and the one we have may not have any hover info. So, if thats the case, try
		// getting the hover info from the next text region along.
		if (info == null && textRegion.getCursorPosition() > 0) {
			textRegion = TextRegionBuilder.getTextRegionAtCursor(wikiEditor, textViewer.getDocument(), region.getOffset() + 1);
			info = getHoverInfo(textRegion);
		}
		return info;
	}

	private String getHoverInfo(TextRegion textRegion) {
		String hoverInfo = (String) textRegion.accept(new GenericTextRegionVisitor(null) {
			public Object visit(WikiWordTextRegion wikiNameTextRegion) {
				return getHoverText(wikiNameTextRegion);
			}

			public Object visit(EclipseResourceTextRegion eclipseResourceTextRegion) {
				return getEclipseResourceHover(eclipseResourceTextRegion);
			}

			public Object visit(PluginResourceTextRegion pluginResourceTextRegion) {
				return getPluginResourceHover(pluginResourceTextRegion);
			}
		});
		return hoverInfo;
	}

	private String getHoverText(WikiLinkTextRegion wikiNameTextRegion) {
		String wikiName = wikiNameTextRegion.getText();
		try {
			IFile file = wikiEditor.getContext().getFileForWikiName(wikiName);
			if (file == null || !file.exists()) {
				return "Open link to create " + wikiName;
			}
			return getHoverText(file);
		} catch (Exception e) {
			WikiPlugin.getDefault().logAndReport("Hover Error", e.getLocalizedMessage(), e);
			return "";
		}
	}

	private String getEclipseResourceHover(EclipseResourceTextRegion eclipseResourceTextRegion) {
		String resourceLink = eclipseResourceTextRegion.getText();
		String relPath = new String(resourceLink.substring(WikiConstants.ECLIPSE_PREFIX.length()));
		return getHoverForEclipseResource(relPath);
	}

	private String getPluginResourceHover(PluginResourceTextRegion textRegion) {
		String resourceLink = new String(textRegion.getText().substring(WikiConstants.PLUGIN_PREFIX.length()));
		String relPath = PluginPathFinder.getPluginPath(resourceLink).toString();
		return getHoverForEclipseResource(relPath);
	}

	private String getHoverForEclipseResource(String relativePath) {
		relativePath = stripLineNumber(relativePath);
		try {
			if (relativePath.length() > 0) {
				IFile file = Resources.findFileInWorkspace(relativePath);
				if (file != null) {
					return getHoverText(file);
				}
				return relativePath.toString();
			}
		} catch (Exception e) {
			WikiPlugin.getDefault().logAndReport("Hover Error", "Cannot get hover info for " + relativePath, e);
		}
		return "";
	}

	private String stripLineNumber(String relPath) {
		if (relPath.lastIndexOf(WikiConstants.LINE_NUMBER_SEPARATOR) > 0) {
			relPath = new String(relPath.substring(0, relPath.lastIndexOf(WikiConstants.LINE_NUMBER_SEPARATOR)));
		}
		return relPath;
	}

	private String getHoverText(IFile file) throws CoreException, IOException {
		if (Resources.isWikiFile(file) || file.getName().endsWith(".txt")) {
			String contents = Resources.getContents(file.getContents());
			if (contents.length() > 0) {
				int length = Math.min(WikiPlugin.getDefault().getPreferenceStore().getInt(WikiConstants.HOVER_PREVIEW_LENGTH), contents.length());
				return new String(contents.substring(0, length));
			}
		}
		return file.getLocation().toString();
	}

}