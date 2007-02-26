package com.teaminabox.eclipse.wiki.actions;

import com.teaminabox.eclipse.wiki.editors.WikiEditor;

public final class PreviewAction extends WikiWorkbenchWindowActionDelegate {

	@Override
	protected void performAction(WikiEditor editor) {
		editor.openPreview();
	}
}