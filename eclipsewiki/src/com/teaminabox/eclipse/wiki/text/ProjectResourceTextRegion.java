package com.teaminabox.eclipse.wiki.text;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.rules.IToken;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.editors.ColourManager;

/**
 * A region of text referring to a resource relative to a project in the Eclipse workspace.
 */
public class ProjectResourceTextRegion extends TextRegion {

	private final IResource	resource;

	public ProjectResourceTextRegion(String text, IResource resource) {
		super(text);
		this.resource = resource;
	}

	@Override
	public <T> T accept(TextRegionVisitor<T> textRegionVisitor) {
		return textRegionVisitor.visit(this);
	}

	@Override
	public IToken getToken(ColourManager colourManager) {
		return getToken(WikiConstants.ECLIPSE_RESOURCE, colourManager);
	}

	@Override
	public boolean isLink() {
		return true;
	}

	public IResource getResource() {
		return resource;
	}

	public String asEclipseLocation() {
		return WikiConstants.ECLIPSE_PREFIX + resource.getFullPath().toString();
	}

}
