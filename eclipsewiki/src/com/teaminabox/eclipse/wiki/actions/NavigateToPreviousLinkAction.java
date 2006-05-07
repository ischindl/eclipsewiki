package com.teaminabox.eclipse.wiki.actions;

import com.teaminabox.eclipse.wiki.editors.WikiEditor;

public final class NavigateToPreviousLinkAction extends WikiWorkbenchWindowActionDelegate {

	protected void performAction(WikiEditor editor) {
		editor.navigateToPreviousLink();
	}
}