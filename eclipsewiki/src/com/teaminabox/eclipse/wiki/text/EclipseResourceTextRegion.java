package com.teaminabox.eclipse.wiki.text;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.text.rules.IToken;

import com.teaminabox.eclipse.wiki.WikiConstants;

/**
 * A region of text referring to a resource in the Eclipse workspace.
 */
public final class EclipseResourceTextRegion extends TextRegion {

	public EclipseResourceTextRegion(String text) {
		super(text);
	}

	/**
	 * @see com.teaminabox.eclipse.wiki.text.TextRegion#getToken(ColourManager colourManager)
	 */
	public IToken getToken(ColourManager colourManager) {
		return getToken(WikiConstants.ECLIPSE_RESOURCE, colourManager);
	}

	/**
	 * @see com.teaminabox.eclipse.wiki.text.TextRegion#accept(com.teaminabox.eclipse.wiki.text.TextRegionVisitor)
	 */
	public Object accept(TextRegionVisitor textRegionVisitor) {
		return textRegionVisitor.visit(this);
	}

	/**
	 * This is a special Wiki region of text.
	 * 
	 * @return <code>true</code>
	 */
	public boolean isLink() {
		return true;
	}

	public boolean resourceExists() {
		IResource resource = getResource();
		return resource != null && resource.exists();
	}

	public IResource getResource() {
		IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(new String(getText().substring(WikiConstants.ECLIPSE_PREFIX.length())));
		return resource;
	}

}