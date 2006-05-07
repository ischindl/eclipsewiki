package com.teaminabox.eclipse.wiki.text;

import java.io.File;

import org.eclipse.core.resources.ResourcesPlugin;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.editors.WikiDocumentContext;

/**
 * I match links to eclipse resources.
 * <P>
 * The resource must exist for there to be a match
 */
public final class EclipseResourceMatcher extends ResourceMatcher {

	/**
	 * @param prefix
	 */
	public EclipseResourceMatcher() {
		super(WikiConstants.ECLIPSE_PREFIX);
	}

	public TextRegion createTextRegion(String text, WikiDocumentContext context) {
		if (accepts(text, context)) {
			return new EclipseResourceTextRegion(text.substring(0, matchLength(text, context)));
		}
		return null;
	}

	protected File findResourceFromPath(String section) {
		try {
			return ResourcesPlugin.getWorkspace().getRoot().findMember(section).getLocation().toFile();
		} catch (Exception ex) {
			return null;
		}
	}

}