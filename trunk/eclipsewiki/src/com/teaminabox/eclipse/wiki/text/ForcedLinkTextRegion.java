package com.teaminabox.eclipse.wiki.text;

public final class ForcedLinkTextRegion extends WikiLinkTextRegion {

	private String	link;
	private String	displayText;

	public ForcedLinkTextRegion(String text, int brackets) {
		super(text);
		int end = getText().indexOf(']');
		displayText = getText().substring(brackets, end);
		link = displayText.replaceAll(" ", "");
	}

	public String getWikiDocumentName() {
		return link.replaceAll("[|]", "");
	}

	public Object accept(TextRegionVisitor textRegionVisitor) {
		return textRegionVisitor.visit(this);
	}

	public String getDisplayText() {
		return displayText;
	}

}
