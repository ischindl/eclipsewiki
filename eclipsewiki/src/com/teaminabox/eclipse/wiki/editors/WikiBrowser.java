package com.teaminabox.eclipse.wiki.editors;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.WikiPlugin;
import com.teaminabox.eclipse.wiki.renderer.ContentRenderer;
import com.teaminabox.eclipse.wiki.renderer.IdeLinkMaker;
import com.teaminabox.eclipse.wiki.renderer.RendererFactory;

public final class WikiBrowser extends ViewPart implements IPropertyChangeListener {

	/**
	 * A helper that can ignore events. It is needed because the browser fires events for all the resources a URL
	 * requires, not just the URL itself. This is problematic because I need to manage the history manually, and don't
	 * want lots of stuff appearing in the history that shouldn't. The history is populated in part by location events
	 * because the events are the only way to know that a link has been clicked in the browser.
	 * <P>
	 * The pattern to set LocationListener.listen to false prior to loading external URLs and then setting back to true
	 * with the browser has finished loading the page. This is determined by the progress monitor...nasty!
	 */
	private class LocationListener extends LocationAdapter {
		boolean	listen	= true;

		public void changing(LocationEvent event) {
			if (listen) {
				WikiBrowser.this.followLink(event);
			}
		}
	}

	private Cursor				waiter;
	private WikiEditor			editor;
	private FormToolkit			toolkit;
	private ContentRenderer		browserContentRenderer;
	private Form				browserForm;
	private Browser				browser;
	private Button				launchButton;
	private History				history;
	private Button				forwardButton;
	private Button				backButton;
	private LocationListener	locationListener;
	private Button				refreshButton;
	private Button				stopButton;
	private ProgressBar			progressBar;

	public WikiBrowser(WikiEditor editor) {
		this.editor = editor;
		history = new History();
		history.add(WikiConstants.WIKI_HREF + editor.getContext().getWikiNameBeingEdited());
		WikiPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);
	}

	public void createPartControl(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay());
		browserForm = toolkit.createForm(parent);
		browserForm.getBody().setLayout(new FillLayout());

		Composite contents = toolkit.createComposite(browserForm.getBody());
		toolkit.paintBordersFor(contents);
		contents.setLayout(new GridLayout(1, true));

		createButtons(contents);

		browser = new Browser(contents, SWT.NONE);
		browser.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		toolkit.adapt(browser, true, true);
		browser.setLayoutData(new GridData(GridData.FILL_BOTH));
		locationListener = new LocationListener();
		browser.addLocationListener(locationListener);

		addProgressBar(contents);
		createBrowserRenderer();
		waiter = new Cursor(Display.getDefault(), SWT.CURSOR_WAIT);
	}

	private void addProgressBar(Composite parent) {
		progressBar = new ProgressBar(parent, SWT.NONE);
		progressBar.setLayoutData(new GridData(GridData.BEGINNING));
		browser.addProgressListener(new ProgressListener() {
			public void changed(ProgressEvent event) {
				if (event.total == 0) {
					return;
				}
				browser.setCursor(waiter);
				int ratio = event.current * 100 / event.total;
				progressBar.setSelection(ratio);
			}

			public void completed(ProgressEvent event) {
				progressBar.setSelection(0);
				locationListener.listen = true;
				stopButton.setEnabled(false);
				browser.setCursor(null);
			}
		});
	}

	private void createButtons(Composite contents) {
		Composite buttonComposite = toolkit.createComposite(contents);
		buttonComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		buttonComposite.setLayout(new GridLayout(6, false));

		Button homeButton = createButton(buttonComposite, WikiPlugin.getResourceString("WikiBrowser.wikiHome"), WikiPlugin.getResourceString("WikiBrowser.wikiHomeTooltip"), true, null); //$NON-NLS-1$ //$NON-NLS-2$
		homeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				history.add(WikiConstants.WIKI_HREF + editor.getContext().getWikiNameBeingEdited());
				enableButtons(false);
				redrawWebView();
			}
		});

		backButton = createButton(buttonComposite, WikiPlugin.getResourceString("WikiBrowser.back"), WikiPlugin.getResourceString("WikiBrowser.backTooltip"), false, ISharedImages.IMG_TOOL_BACK); //$NON-NLS-1$ //$NON-NLS-2$
		backButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				WikiBrowser.this.goBack();
			}
		});

		forwardButton = createButton(buttonComposite, WikiPlugin.getResourceString("WikiBrowser.forward"), WikiPlugin.getResourceString("WikiBrowser.forwardTooltip"), false, ISharedImages.IMG_TOOL_FORWARD); //$NON-NLS-1$ //$NON-NLS-2$
		forwardButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				WikiBrowser.this.goForward();
			}
		});

		refreshButton = createButton(buttonComposite, WikiPlugin.getResourceString("WikiBrowser.refresh"), WikiPlugin.getResourceString("WikiBrowser.refreshTooltip"), false, ISharedImages.IMG_TOOL_REDO); //$NON-NLS-1$ //$NON-NLS-2$
		refreshButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				WikiBrowser.this.refresh();
			}
		});

		stopButton = createButton(buttonComposite, WikiPlugin.getResourceString("WikiBrowser.stop"), WikiPlugin.getResourceString("WikiBrowser.stopTooltip"), false, ISharedImages.IMG_TOOL_DELETE); //$NON-NLS-1$ //$NON-NLS-2$
		stopButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				WikiBrowser.this.browser.stop();
				WikiBrowser.this.progressBar.setSelection(0);
			}
		});

		launchButton = createButton(buttonComposite, WikiPlugin.getResourceString("WikiBrowser.launch"), WikiPlugin.getResourceString("WikiBrowser.launchTooltip"), false, ISharedImages.IMG_TOOL_UP); //$NON-NLS-1$ //$NON-NLS-2$
		launchButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Program.launch(browser.getUrl());
			}
		});
	}

	private Button createButton(Composite buttonComposite, String label, String toolTip, boolean enabled, String sharedImagesConstant) {
		Button button = toolkit.createButton(buttonComposite, label, SWT.PUSH);
		button.setLayoutData(new GridData(GridData.BEGINNING));
		button.setToolTipText(toolTip);
		button.setEnabled(enabled);
		button.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(sharedImagesConstant));
		return button;
	}

	private void followLink(LocationEvent event) {
		locationListener.listen = false;
		backButton.setEnabled(true);
		String location = event.location;

		if (WikiBrowser.isWikiLocation(location)) {
			openWikiLocation(location);
			event.doit = false;
			if (WikiPlugin.getDefault().getPreferenceStore().getBoolean(WikiConstants.REUSE_EDITOR)) {
				history.add(event.location);
			}
		} else if (!"about:blank".equals(location)) { //$NON-NLS-1$
			history.add(event.location);
			enableButtons(true);
		}
	}

	private void openWikiLocation(String location) {
		String wikiDoc = new String(location.substring(WikiConstants.WIKI_HREF.length()));
		if (wikiDoc.endsWith("/")) { //$NON-NLS-1$
			wikiDoc = new String(wikiDoc.substring(0, wikiDoc.length() - 1));
		}

		if (wikiDoc.startsWith(WikiConstants.JAVA_LINK_PREFIX)) {
			new WikiLinkLauncher(editor).openJavaType(wikiDoc.substring(WikiConstants.JAVA_LINK_PREFIX.length()));
		} else if (wikiDoc.startsWith(WikiConstants.ECLIPSE_PREFIX)) {
			String escaped = wikiDoc.replaceAll("%20", " "); //$NON-NLS-1$ //$NON-NLS-2$
			new WikiLinkLauncher(editor).openEclipseLocation(escaped);
		} else if (wikiDoc.startsWith(WikiConstants.PLUGIN_PREFIX)) {
			String escaped = wikiDoc.replaceAll("%20", " "); //$NON-NLS-1$ //$NON-NLS-2$
			new WikiLinkLauncher(editor).openPluginLocation(escaped);
		} else {
			try {
				new WikiLinkLauncher(editor).openWikiDocument(wikiDoc);
			} catch (Exception e) {
				WikiPlugin.getDefault().logAndReport(WikiPlugin.getResourceString(WikiConstants.RESOURCE_WIKI_ERROR_DIALOGUE_OPEN_WIKI_FILE_TITLE), WikiPlugin.getResourceString(WikiConstants.RESOURCE_WIKI_ERROR_DIALOGUE_OPEN_WIKI_FILE_TEXT), e);
			}
		}
		enableButtons(false);
		browser.setCursor(null);
		locationListener.listen = true;
	}

	private void enableButtons(boolean state) {
		launchButton.setEnabled(state);
		refreshButton.setEnabled(state);
		stopButton.setEnabled(state);
	}

	private void goForward() {
		locationListener.listen = false;
		if (history.hasNext()) {
			String location = (String) history.next();
			openLocation(location);
			backButton.setEnabled(true);
			forwardButton.setEnabled(history.hasNext());
		}
	}

	private void goBack() {
		locationListener.listen = false;
		if (history.hasPrevious()) {
			String location = (String) history.back();
			openLocation(location);
			forwardButton.setEnabled(true);
			backButton.setEnabled(history.hasPrevious());
		} else {
			redrawWebView();
		}
	}

	private void refresh() {
		locationListener.listen = false;
		browser.refresh();
	}

	private void openLocation(String location) {
		if (WikiBrowser.isWikiLocation(location)) {
			openWikiLocation(location);
		} else {
			browser.setUrl(location);
			enableButtons(true);
		}
	}

	private static boolean isWikiLocation(String location) {
		return location.startsWith(WikiConstants.WIKI_HREF);
	}

	void redrawWebView() {
		browser.setCursor(waiter);
		String text = browserContentRenderer.render(editor.getContext(), new IdeLinkMaker(editor.getContext()), false);
		browser.setText(text);
		enableButtons(false);
		browser.setCursor(null);
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (WikiConstants.BROWSER_RENDERER.equals(event.getProperty()) && !browserContentRenderer.getClass().getName().equals(event.getNewValue())) {
			createBrowserRenderer();
			redrawWebView();
		} else if (WikiConstants.BROWSER_CSS_URL.equals(event.getProperty())) {
			redrawWebView();
		}
	}

	private void createBrowserRenderer() {
		browserContentRenderer = RendererFactory.createContentRenderer();
	}

	public void setFocus() {
		browser.setFocus();
	}

	public void dispose() {
		waiter.dispose();
		WikiPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(this);
		browser.dispose();
		toolkit.dispose();
	}

}