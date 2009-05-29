package com.teaminabox.eclipse.wiki.text;

import java.io.File;

import org.eclipse.core.resources.IResource;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.editors.WikiDocumentContext;

public class ProjectResourceMatcher extends ResourceMatcher {

	public ProjectResourceMatcher() {
		super(WikiConstants.PROJECT_PREFIX);
	}

	@Override
	public TextRegion createTextRegion(String text, WikiDocumentContext context) {
		if (!accepts(text, context)) {
			return null;
		}
		String match = new String(text.substring(0, matchLength(text, context)));
		String path = match.substring(WikiConstants.PROJECT_PREFIX.length());
		IResource member = context.getProject().findMember(path);
		if (member != null && member.exists() && member.getType() == IResource.FILE) {
			return new ProjectResourceTextRegion(match, member);
		}
		return null;
	}

	@Override
	protected File findResourceFromPath(WikiDocumentContext context, String section) {
		try {
			return context.getProject().findMember(section).getLocation().toFile();
		} catch (Exception ex) {
			return null;
		}
	}

}
