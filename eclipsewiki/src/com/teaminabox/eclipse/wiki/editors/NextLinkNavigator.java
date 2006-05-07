package com.teaminabox.eclipse.wiki.editors;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.WikiPlugin;
import com.teaminabox.eclipse.wiki.text.TextRegion;

public class NextLinkNavigator extends LinkNavigator {

	public NextLinkNavigator(WikiEditor editor) {
		super(editor);
	}

	public void next() {
		try {
			int pos = getSelection().getOffset() + 1;
			if (pos >= getDocument().getLength()) {
				return;
			}
			TextRegion textRegion = getTextRegionAtCursor(pos);
			int endOfCurrentRegion = pos + textRegion.getLength() - textRegion.getCursorPosition();
			pos = endOfCurrentRegion + 1;

			while (pos < getDocument().getLength()) {
				textRegion = getTextRegionAtCursor(pos);
				int textRegionIndex = pos - textRegion.getCursorPosition();

				if (textRegion.getLength() == 0) {
					pos++;
				} else if (textRegion.isLink() && textRegionIndex > endOfCurrentRegion) {
					getEditor().selectAndReveal(pos - textRegion.getCursorPosition(), 0);
					return;
				} else {
					pos = textRegionIndex + textRegion.getLength() + 1;
				}
			}
		} catch (Exception e) {
			WikiPlugin.getDefault().logAndReport(WikiPlugin.getResourceString(WikiConstants.RESOURCE_WIKI_ERROR_DIALOGUE_PROGRAMMATIC_ERROR_TITLE), WikiPlugin.getResourceString(WikiConstants.RESOURCE_WIKI_ERROR_DIALOGUE_PROGRAMMATIC_ERROR_TEXT), e);
		}
	}

}
