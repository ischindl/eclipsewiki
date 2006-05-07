package com.teaminabox.eclipse.wiki.text;

/**
 * A region of text that is a standard Wiki name.
 */
public final class WikiNameTextRegion extends WikiLinkTextRegion {

	public WikiNameTextRegion(String text) {
		super(text);
	}

	public String getWikiDocumentName() {
		return getText();
	}

	public Object accept(TextRegionVisitor textRegionVisitor) {
		return textRegionVisitor.visit(this);
	}

	public String getDisplayText() {
		return getText();
	}

}