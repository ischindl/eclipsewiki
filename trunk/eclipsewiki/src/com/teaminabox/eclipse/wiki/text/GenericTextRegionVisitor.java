package com.teaminabox.eclipse.wiki.text;

/**
 * A TextRegionVisitor that returns a default value for each TextRegionVisitor method.
 */
public class GenericTextRegionVisitor implements TextRegionVisitor {

	private Object	defaultReturnValue;

	public GenericTextRegionVisitor(Object defaultReturnValue) {
		this.defaultReturnValue = defaultReturnValue;
	}

	public Object visit(UndefinedTextRegion undefinedTextRegion) {
		return defaultReturnValue;
	}

	public Object visit(UrlTextRegion urlTextRegion) {
		return defaultReturnValue;
	}

	public Object visit(WikiNameTextRegion wikiNameTextRegion) {
		return defaultReturnValue;
	}

	public Object visit(WikiUrlTextRegion wikiUrlTextRegion) {
		return defaultReturnValue;
	}

	public Object visit(BasicTextRegion basicTextRegion) {
		return defaultReturnValue;
	}

	public Object visit(EclipseResourceTextRegion eclipseResourceTextRegion) {
		return defaultReturnValue;
	}

	public Object visit(PluginResourceTextRegion eclipseResourceTextRegion) {
		return defaultReturnValue;
	}

	public Object visit(JavaTypeTextRegion region) {
		return defaultReturnValue;
	}

	public Object visit(ForcedLinkTextRegion region) {
		return defaultReturnValue;
	}
}