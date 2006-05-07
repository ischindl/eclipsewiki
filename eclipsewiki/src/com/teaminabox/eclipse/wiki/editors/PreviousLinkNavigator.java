package com.teaminabox.eclipse.wiki.editors;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.WikiPlugin;
import com.teaminabox.eclipse.wiki.text.TextRegion;

public class PreviousLinkNavigator extends LinkNavigator {

	public PreviousLinkNavigator(WikiEditor editor) {
		super(editor);
	}

	public void previous() {
		try {
			int pos = getSelection().getOffset();
			TextRegion textRegion = getTextRegionAtCursor(pos);
			if (textRegion.getLocationInDocument() == 0) {
				return;
			}
			pos = textRegion.getLocationInDocument() - 1;

			do {
				textRegion = getTextRegionAtCursor(pos);
				if (textRegion.isLink()) {
					getEditor().selectAndReveal(textRegion.getLocationInDocument(), 0);
					return;
				} else if (textRegion.getLength() == 0) {
					pos--;
				} else {
					pos = textRegion.getLocationInDocument() - 1;
				}
				if (pos < 0) {
					return;
				}
			} while (pos > 0 && !textRegion.isLink());

		} catch (Exception e) {
			WikiPlugin.getDefault().logAndReport(WikiPlugin.getResourceString(WikiConstants.RESOURCE_WIKI_ERROR_DIALOGUE_PROGRAMMATIC_ERROR_TITLE), WikiPlugin.getResourceString(WikiConstants.RESOURCE_WIKI_ERROR_DIALOGUE_PROGRAMMATIC_ERROR_TEXT), e);
		}
	}

}
