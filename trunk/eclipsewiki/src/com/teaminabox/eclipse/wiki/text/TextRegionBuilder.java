package com.teaminabox.eclipse.wiki.text;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.teaminabox.eclipse.wiki.editors.WikiDocumentContext;
import com.teaminabox.eclipse.wiki.editors.WikiEditor;
import com.teaminabox.eclipse.wiki.renderer.RendererFactory;

/**
 * Builds {@link TextRegion text regions}.
 */
public final class TextRegionBuilder {

	private TextRegionBuilder() {
	}

	public static TextRegion getFirstTextRegion(String text, WikiDocumentContext context) {
		if (text.length() == 0) {
			return new UndefinedTextRegion("");
		}
		TextRegion[] candidates = TextRegionBuilder.getCandidates(text, context);
		int length = 0;
		int best = 0;
		for (int i = 0; i < candidates.length; i++) {
			int candidateLength = candidates[i].getLength();
			if (candidateLength > length) {
				best = i;
				length = candidateLength;
			}
		}
		return candidates[best];
	}

	public static TextRegion[] getTextRegions(String text, WikiDocumentContext context) {
		ArrayList list = new ArrayList();
		int start = 0;
		while (start < text.length()) {
			TextRegion region = TextRegionBuilder.getFirstTextRegion(new String(text.substring(start)), context);
			list.add(region);
			start += region.getLength();
		}
		return (TextRegion[]) list.toArray(new TextRegion[list.size()]);
	}

	private static TextRegion[] getCandidates(String text, WikiDocumentContext context) {
		ArrayList candidates = new ArrayList();
		TextRegionMatcher[] matchers = RendererFactory.createContentRenderer().getRendererMatchers();
		for (int i = 0; i < matchers.length; i++) {
			TextRegion textRegion = matchers[i].createTextRegion(text, context);
			if (textRegion != null) {
				candidates.add(textRegion);
			}
		}
		return (TextRegion[]) candidates.toArray(new TextRegion[candidates.size()]);
	}

	public static TextRegion getTextRegionAtCursor(WikiEditor editor, IDocument document, int initialPos) {
		try {
			int pos = initialPos;
			int line = document.getLineOfOffset(pos);
			int start = document.getLineOffset(line);
			int end = start + document.getLineInformation(line).getLength();

			/*
			 * The line does not include \n or \r so pos can be > end. Making pos = end in this case is safe for the
			 * purposes of determining the TextRegion at the cursor position
			 */
			if (pos > end) {
				pos = end;
			}

			String word = document.get(start, end - start);
			TextRegion textRegion = TextRegionBuilder.getFirstTextRegion(word, editor.getContext());
			textRegion.setCursorPosition(pos - start);
			textRegion.setLocationInDocument(start);
			// Move through TextRegions until the cursor is in the TextRegion
			while (start + textRegion.getLength() < pos) {
				start = start + textRegion.getLength();
				word = document.get(start, end - start);
				textRegion = TextRegionBuilder.getFirstTextRegion(word, editor.getContext());
				textRegion.setCursorPosition(pos - start);
				textRegion.setLocationInDocument(start);
			}
			return textRegion;
		} catch (BadLocationException e) {
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			MessageDialog.openError(shell, "TextRegionBuilder Error", e.getLocalizedMessage());
			return new UndefinedTextRegion("");
		}
	}

}