package com.teaminabox.eclipse.wiki.editors;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextViewer;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.WikiPlugin;

public final class WikiDoubleClickStrategy implements ITextDoubleClickStrategy {

	protected ITextViewer	textViewer;

	public void doubleClicked(ITextViewer part) {
		int pos = part.getSelectedRange().x;
		if (pos < 0) {
			return;
		}
		textViewer = part;
		selectWord(pos);
	}

	protected boolean selectWord(int caretPos) {
		IDocument doc = textViewer.getDocument();
		int startPos;
		int endPos;
		try {
			startPos = getStartOfWord(doc, caretPos);
			endPos = getEndOfWord(caretPos, doc);
			selectRange(startPos, endPos);
			return true;

		} catch (BadLocationException x) {
			WikiPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, WikiConstants.PLUGIN_ID, IStatus.ERROR, "Wiki Editor Error", x));
		}
		return false;
	}

	private int getEndOfWord(int caretPos, IDocument doc) throws BadLocationException {
		int pos = caretPos;
		int length = doc.getLength();
		while (pos < length) {
			char c = doc.getChar(pos);
			if (!Character.isLetterOrDigit(c)) {
				break;
			}
			++pos;
		}
		return pos;
	}

	private int getStartOfWord(IDocument doc, int caretPos) throws BadLocationException {
		int pos = caretPos;
		char c;
		while (pos >= 0) {
			c = doc.getChar(pos);
			if (!Character.isLetterOrDigit(c)) {
				break;
			}
			--pos;
		}
		return pos;
	}

	private void selectRange(int startPos, int stopPos) {
		int offset = startPos + 1;
		int length = stopPos - offset;
		textViewer.setSelectedRange(offset, length);
	}
}