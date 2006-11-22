package com.teaminabox.eclipse.wiki.renderer;

import static com.teaminabox.eclipse.wiki.util.Resources.existsAsFile;
import static com.teaminabox.eclipse.wiki.util.Resources.getContents;

import java.io.StringReader;
import java.io.StringWriter;

import org.eclipse.core.resources.IFile;

import com.teaminabox.eclipse.wiki.WikiPlugin;
import com.teaminabox.eclipse.wiki.editors.WikiDocumentContext;
import com.teaminabox.eclipse.wiki.text.EclipseResourceTextRegion;
import com.teaminabox.eclipse.wiki.text.GenericTextRegionVisitor;
import com.teaminabox.eclipse.wiki.text.JavaTypeTextRegion;
import com.teaminabox.eclipse.wiki.text.TextRegion;
import com.teaminabox.eclipse.wiki.text.WikiWordTextRegion;
import com.teaminabox.eclipse.wiki.util.JavaUtils;

public final class EmbeddedTextRegionAppender extends GenericTextRegionVisitor<String> {

	private final TextRegion			region;
	private final LinkMaker				linkMaker;
	private final WikiDocumentContext	context;

	public EmbeddedTextRegionAppender(TextRegion region, WikiDocumentContext context, LinkMaker linkMaker) {
		super(region.getDisplayText());
		this.context = context;
		this.linkMaker = linkMaker;
		this.region = region;
	}

	@Override
	public String visit(WikiWordTextRegion wikiNameTextRegion) {
		try {
			IFile file = context.getFileForWikiName(region.getText());
			if (file == null) {
				return region.getText();
			} else {
				return RendererFactory.createContentRenderer().render(new WikiDocumentContext(file), linkMaker, true);
			}
		} catch (Exception e) {
			return report(e);
		}
	}

	@Override
	public String visit(JavaTypeTextRegion region) {
		try {
			String contents = region.getType().getCompilationUnit().getBuffer().getContents();
			StringWriter writer = new StringWriter();
			JavaUtils.writeJava(new StringReader(contents), writer);
			return writer.toString();
		} catch (Exception e) {
			report(e);
			return super.visit(region);
		}
	}

	@Override
	public String visit(EclipseResourceTextRegion eclipseResourceTextRegion) {
		try {
			if (existsAsFile(eclipseResourceTextRegion.getResource())) {
				return getContents((IFile) eclipseResourceTextRegion.getResource());
			}
		} catch (Exception e) {
			report(e);
		}
		return super.visit(eclipseResourceTextRegion);
	}

	private String report(Exception e) {
		WikiPlugin.getDefault().log("Could not append contents", e);
		return region.getText() + "(error embedding contents, please see logs)";
	}

}