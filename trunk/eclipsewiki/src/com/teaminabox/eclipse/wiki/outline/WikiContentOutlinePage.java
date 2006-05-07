package com.teaminabox.eclipse.wiki.outline;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import com.teaminabox.eclipse.wiki.editors.WikiEditor;

public final class WikiContentOutlinePage extends ContentOutlinePage implements IPropertyListener {

	private IFile		input;
	private WikiEditor	editor;

	public WikiContentOutlinePage(WikiEditor editor) {
		this.editor = editor;
		editor.addPropertyListener(this);
		getInput();
	}

	private void getInput() {
		if (editor != null && editor.getEditorInput() != null) {
			this.input = ((IFileEditorInput) editor.getEditorInput()).getFile();
		}
	}

	public void dispose() {
		if (editor != null) {
			editor.removePropertyListener(this);
		}
		super.dispose();
	}

	public void createControl(Composite parent) {
		super.createControl(parent);

		final TreeViewer viewer = getTreeViewer();
		viewer.setContentProvider(new WorkbenchContentProvider());
		viewer.setLabelProvider(new WorkbenchLabelProvider());
		viewer.setInput(getContentOutline(input));

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection() instanceof StructuredSelection) {
					WikiContentOutlinePage.this.treeItemSelected((StructuredSelection) event.getSelection());
				}
			}
		});

		viewer.expandAll();

		registerContextMenu(viewer);
	}

	private void registerContextMenu(final TreeViewer viewer) {
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS + "-end")); //$NON-NLS-1$

		Menu menu = menuMgr.createContextMenu(viewer.getTree());
		viewer.getTree().setMenu(menu);
		// Be sure to register it so that other plug-ins can add actions.
		getSite().registerContextMenu(getClass().getName(), menuMgr, viewer); 

	}

	private void treeItemSelected(StructuredSelection selection) {
		MarkElement element = (MarkElement) selection.getFirstElement();
		if (element != null) {
			editor.selectAndReveal(element.getStart(), element.getLength());
		}
	}

	/**
	 * Gets the content outline for a given input element. Returns the outline (a list of MarkElements), or null if the
	 * outline could not be generated.
	 */
	private IAdaptable getContentOutline(IAdaptable input) {
		return WikiModelFactory.getInstance().getContentOutline(input, editor);
	}

	private void update() {
		getInput();
		if (input == null || getControl().isDisposed()) {
			return;
		}
		getControl().setRedraw(false);
		getTreeViewer().setInput(getContentOutline(input));
		getTreeViewer().expandAll();
		getControl().setRedraw(true);
	}

	public void propertyChanged(Object source, int propId) {
		Display.getCurrent().asyncExec(new Runnable() {
			public void run() {
				update();
			}
		});
	}

}