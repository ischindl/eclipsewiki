package com.teaminabox.eclipse.wiki.editors;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IReusableEditor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.WikiPlugin;
import com.teaminabox.eclipse.wiki.outline.WikiContentOutlinePage;
import com.teaminabox.eclipse.wiki.renderer.RendererFactory;

public final class WikiBrowserEditor extends MultiPageEditorPart implements IReusableEditor, IResourceChangeListener, IPropertyChangeListener {

	private WikiEditor	editor;
	private int			browserIndex;
	private int			sourceIndex;
	private WikiBrowser	wikiBrowser;
	private Browser		syntaxBrowser;
	private int			syntaxIndex;

	public WikiBrowserEditor() {
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	public void dispose() {
		WikiPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(this);
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		wikiBrowser.dispose();
		editor.dispose();
		super.dispose();
	}

	private void createSourcePage() {
		try {
			editor = new WikiEditor();
			editor.setReusableEditor(this);
			sourceIndex = addPage(editor, getEditorInput());
			setPageText(sourceIndex, "Source");
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(), "Error creating nested text editor", null, e.getStatus());
		}
	}

	private void createBrowserPage() {
		wikiBrowser = new WikiBrowser(editor);
		Composite composite = new Composite(getContainer(), SWT.NULL);
		composite.setLayout(new FillLayout());
		wikiBrowser.createPartControl(composite);
		browserIndex = addPage(composite);
		setPageText(browserIndex, "Browser");
	}

	protected void createPages() {
		createSourcePage();
		createBrowserPage();
		createSyntaxPage();
		setActivePage(sourceIndex);
	}

	private void createSyntaxPage() {
		Composite composite = new Composite(getContainer(), SWT.NULL);
		composite.setLayout(new FillLayout());
		syntaxBrowser = new Browser(composite, SWT.NONE);
		initialiseSyntaxBrowser();
		WikiPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);
		syntaxIndex = addPage(composite);
		setPageText(syntaxIndex, "Syntax");
	}

	public void doSave(IProgressMonitor monitor) {
		getEditor(0).doSave(monitor);
	}

	public void doSaveAs() {
		IEditorPart editor = getEditor(0);
		editor.doSaveAs();
		setPageText(0, editor.getTitle());
		setInput(editor.getEditorInput());
	}

	public void gotoMarker(IMarker marker) {
		setActivePage(0);
		IDE.gotoMarker(getEditor(0), marker);
	}

	public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException {
		if (!(editorInput instanceof IFileEditorInput)) {
			throw new PartInitException("Invalid Input: Must be IFileEditorInput");
		}
		super.init(site, editorInput);
		setEditorTitle();
	}

	private void setEditorTitle() {
		if (getEditorInput() != null) {
			setPartName(((IFileEditorInput) getEditorInput()).getFile().getName());
		}
	}

	public void setInput(IEditorInput newInput) {
		super.setInputWithNotify(newInput);
		if (editor != null) {
			editor.setInput(newInput);
			wikiBrowser.redrawWebView();
			setEditorTitle();
		}
	}

	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);
		if (newPageIndex == browserIndex) {
			wikiBrowser.redrawWebView();
		}
	}

	public boolean isSaveAsAllowed() {
		return true;
	}

	/**
	 * Closes all project files on project close.
	 */
	public void resourceChanged(final IResourceChangeEvent event) {
		if (event.getType() == IResourceChangeEvent.PRE_CLOSE) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
					for (int i = 0; i < pages.length; i++) {
						if (((FileEditorInput) editor.getEditorInput()).getFile().getProject().equals(event.getResource())) {
							IEditorPart editorPart = pages[i].findEditor(editor.getEditorInput());
							pages[i].closeEditor(editorPart, true);
						}
					}
				}
			});
		}
	}

	public WikiEditor getEditor() {
		return editor;
	}

	public boolean isEditingSource() {
		return getActivePage() == sourceIndex;
	}

	public void propertyChange(PropertyChangeEvent event) {
		initialiseSyntaxBrowser();
	}
	
	private void initialiseSyntaxBrowser() {
		String renderer = RendererFactory.getContentRendererName();
		IPath path = new Path(WikiConstants.HELP_PATH).append(renderer + ".html");
		try {
			syntaxBrowser.setText(WikiPlugin.getDefault().loadTextContents(path));
		} catch (Exception e) {
			WikiPlugin.getDefault().log("Unable to load syntax", e);
			syntaxBrowser.setText(e.getLocalizedMessage());
		}
	}

	public Object getAdapter(Class key) {
		if (key.equals(IContentOutlinePage.class)) {
			IEditorInput input = getEditorInput();
			if (input instanceof IFileEditorInput) {
				return new WikiContentOutlinePage(editor);
			}
		}
		return super.getAdapter(key);
	}
}