package com.teaminabox.eclipse.wiki.editors;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.ide.IDEActionFactory;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;

/**
 * Manages the installation/deinstallation of global actions for multi-page editors. Responsible for the redirection of
 * global actions to the active editor. Multi-page contributor replaces the contributors for the individual editors in
 * the multi-page editor.
 */
public final class WikiBrowserEditorContributor extends MultiPageEditorActionBarContributor {
	private IEditorPart	activeEditorPart;

	/**
	 * Creates a multi-page contributor.
	 */
	public WikiBrowserEditorContributor() {
		super();
	}

	/**
	 * Returns the action registed with the given text editor.
	 * 
	 * @return IAction or null if editor is null.
	 */
	protected IAction getAction(ITextEditor editor, String actionID) {
		return editor == null ? null : editor.getAction(actionID);
	}

	/*
	 * (non-JavaDoc) Method declared in AbstractMultiPageEditorActionBarContributor.
	 */

	@Override
	public void setActivePage(IEditorPart part) {
		if (activeEditorPart == part) {
			return;
		}

		activeEditorPart = part;

		IActionBars actionBars = getActionBars();
		if (actionBars != null) {

			WikiEditor editor = part instanceof WikiEditor ? (WikiEditor) part : null;

			actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), getAction(editor, ITextEditorActionConstants.DELETE));
			actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), getAction(editor, ITextEditorActionConstants.UNDO));
			actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), getAction(editor, ITextEditorActionConstants.REDO));
			actionBars.setGlobalActionHandler(ActionFactory.CUT.getId(), getAction(editor, ITextEditorActionConstants.CUT));
			actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), getAction(editor, ITextEditorActionConstants.COPY));
			actionBars.setGlobalActionHandler(ActionFactory.PASTE.getId(), getAction(editor, ITextEditorActionConstants.PASTE));
			actionBars.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(), getAction(editor, ITextEditorActionConstants.SELECT_ALL));
			actionBars.setGlobalActionHandler(ActionFactory.FIND.getId(), getAction(editor, ITextEditorActionConstants.FIND));
			actionBars.setGlobalActionHandler(IDEActionFactory.BOOKMARK.getId(), getAction(editor, IDEActionFactory.BOOKMARK.getId()));
			actionBars.updateActionBars();

			actionBars.setGlobalActionHandler("com.teaminabox.eclipse.wiki.actions.NavigateToNextLinkAction", getAction(editor, "com.teaminabox.eclipse.wiki.actions.NavigateToNextLinkAction"));
		}
	}

	@Override
	public void contributeToMenu(IMenuManager manager) {

	}

	@Override
	public void contributeToToolBar(IToolBarManager manager) {

	}
}