package com.teaminabox.eclipse.wiki.actions;

import com.teaminabox.eclipse.wiki.editors.WikiEditor;

public final class OpenWikiLinkWorkbenchAction extends WikiWorkbenchWindowActionDelegate {

	protected void performAction(WikiEditor editor) {
		editor.openWikiLinkOnSelection();
	}
}