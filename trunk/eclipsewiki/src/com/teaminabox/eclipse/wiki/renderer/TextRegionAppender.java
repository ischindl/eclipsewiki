package com.teaminabox.eclipse.wiki.renderer;

import com.teaminabox.eclipse.wiki.text.BasicTextRegion;
import com.teaminabox.eclipse.wiki.text.EclipseResourceTextRegion;
import com.teaminabox.eclipse.wiki.text.EmbeddedWikiWordTextRegion;
import com.teaminabox.eclipse.wiki.text.ForcedLinkTextRegion;
import com.teaminabox.eclipse.wiki.text.JavaTypeTextRegion;
import com.teaminabox.eclipse.wiki.text.PluginResourceTextRegion;
import com.teaminabox.eclipse.wiki.text.TextRegionVisitor;
import com.teaminabox.eclipse.wiki.text.UndefinedTextRegion;
import com.teaminabox.eclipse.wiki.text.UrlTextRegion;
import com.teaminabox.eclipse.wiki.text.WikiUrlTextRegion;
import com.teaminabox.eclipse.wiki.text.WikiWordTextRegion;

public class TextRegionAppender<T> implements TextRegionVisitor<T> {

	private final LinkMaker					linkMaker;
	private final StringBuffer				buffer;
	private final AbstractContentRenderer	contentRenderer;

	public TextRegionAppender(StringBuffer buffer, LinkMaker linkMaker, AbstractContentRenderer contentRenderer) {
		this.buffer = buffer;
		this.linkMaker = linkMaker;
		this.contentRenderer = contentRenderer;
	}

	public T visit(UndefinedTextRegion undefinedTextRegion) {
		buffer.append(undefinedTextRegion.getText());
		return null;
	}

	public T visit(UrlTextRegion urlTextRegion) {
		buffer.append(linkMaker.make(urlTextRegion));
		return null;
	}

	public T visit(WikiWordTextRegion wikiNameTextRegion) {
		buffer.append(linkMaker.make(wikiNameTextRegion));
		return null;
	}

	public T visit(WikiUrlTextRegion wikiUrlTextRegion) {
		buffer.append(linkMaker.make(wikiUrlTextRegion));
		return null;
	}

	public T visit(BasicTextRegion basicTextRegion) {
		buffer.append(basicTextRegion.getDisplayText());
		return null;
	}

	public T visit(EclipseResourceTextRegion eclipseResourceTextRegion) {
		buffer.append(linkMaker.make(eclipseResourceTextRegion));
		return null;
	}

	public T visit(PluginResourceTextRegion pluginResourceTextRegion) {
		buffer.append(linkMaker.make(pluginResourceTextRegion));
		return null;
	}

	public T visit(JavaTypeTextRegion region) {
		buffer.append(linkMaker.make(region));
		return null;
	}

	public T visit(ForcedLinkTextRegion region) {
		buffer.append(linkMaker.make(region));
		return null;
	}

	public T visit(EmbeddedWikiWordTextRegion region) {
		contentRenderer.embed(region);
		return null;
	}
}