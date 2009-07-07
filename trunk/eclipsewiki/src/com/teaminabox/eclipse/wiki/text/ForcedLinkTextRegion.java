package com.teaminabox.eclipse.wiki.text;

public class ForcedLinkTextRegion extends WikiLinkTextRegion {

	private String	link;

	public ForcedLinkTextRegion(String text, int brackets) {
		super(text);
		int end = getText().indexOf(']');
		setDisplayText(new String(getText().substring(brackets, end)));
		link = getDisplayText().replaceAll(" ", "");
	}

	public String getWikiDocumentName() {
		return link.replaceAll("[|]", "");
	}

	public <T> T accept(TextRegionVisitor<T> textRegionVisitor) {
		return textRegionVisitor.visit(this);
	}

}
