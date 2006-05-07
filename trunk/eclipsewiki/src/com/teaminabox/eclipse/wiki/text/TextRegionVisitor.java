package com.teaminabox.eclipse.wiki.text;

public interface TextRegionVisitor {
	Object visit(UndefinedTextRegion undefinedTextRegion);

	Object visit(UrlTextRegion urlTextRegion);

	Object visit(WikiNameTextRegion wikiNameTextRegion);

	Object visit(WikiUrlTextRegion wikiUrlTextRegion);

	Object visit(BasicTextRegion basicTextRegion);

	Object visit(EclipseResourceTextRegion eclipseResourceTextRegion);

	Object visit(PluginResourceTextRegion pluginResourceTextRegion);

	Object visit(JavaTypeTextRegion region);

	Object visit(ForcedLinkTextRegion region);
}