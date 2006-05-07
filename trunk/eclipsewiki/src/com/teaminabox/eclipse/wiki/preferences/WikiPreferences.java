package com.teaminabox.eclipse.wiki.preferences;

import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.WikiPlugin;

public final class WikiPreferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private static Map	wikispace;

	public WikiPreferences() {
		super(FieldEditorPreferencePage.GRID);
		setDescription(WikiPlugin.getResourceString("WikiSyntaxPreferencePage.description")); //$NON-NLS-1$
		setPreferenceStore(WikiPlugin.getDefault().getPreferenceStore());
	}

	protected void addField(FieldEditor editor) {
		super.addField(editor);
	}

	public void createFieldEditors() {
		Composite composite = new Composite(getFieldEditorParent(), SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		TabFolder folder = new TabFolder(composite, SWT.NONE);

		new EditorPreferences(this, createTab(folder, "WikiPreferences.editorPreferencesTitle"));
		new WikiSpacePreferencePage(createTab(folder, "WikiPreferences.WikiSpacePreferencesTitle"));
		createRendererFieldEditors(createTab(folder, "WikiPreferences.rendererPreferencesTitle"));

		Dialog.applyDialogFont(composite);
	}

	private void createRendererFieldEditors(Composite parent) {
		addField(new StringFieldEditor(WikiConstants.BROWSER_CSS_URL, WikiPlugin.getResourceString(WikiConstants.BROWSER_CSS_URL), parent));
		addField(new ComboListEditor(WikiConstants.BROWSER_RENDERER, WikiPlugin.getResourceString(WikiConstants.BROWSER_RENDERER), getBrowserRendererLabels(), WikiConstants.BROWSER_RENDERERS, parent));
		addField(new BooleanFieldEditor(WikiConstants.RENDER_FULLY_QUALIFIED_TYPE_NAMES, WikiPlugin.getResourceString("renderFullyQualifiedTypeNames"), parent));
	}

	private String[] getBrowserRendererLabels() {
		String[] labels = new String[WikiConstants.BROWSER_RENDERERS.length];
		for (int i = 0; i < labels.length; i++) {
			labels[i] = WikiPlugin.getResourceString(WikiConstants.BROWSER_RENDERERS[i]);
		}
		return labels;
	}

	private Composite createTab(TabFolder folder, String titleKey) {
		TabItem item = new TabItem(folder, SWT.NONE);
		item.setText(WikiPlugin.getResourceString(titleKey));
		Composite c = new Composite(folder, SWT.NONE);
		c.setLayout(new TabFolderLayout());
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		item.setControl(c);
		return c;
	}

	public void init(IWorkbench workbench) {
	}

	public static void init(IPreferenceStore store) {
		wikispace = WikiPreferences.reloadWikiSpaceMap(store);
	}

	public static Map reloadWikiSpaceMap(IPreferenceStore store) {
		TreeMap wikiSpace = new TreeMap();
		String names = store.getString(WikiConstants.WIKISPACE_NAMES);
		String urls = store.getString(WikiConstants.WIKISPACE_URLS);
		StringTokenizer nameTokenizer = new StringTokenizer(names, WikiConstants.WIKISPACE_SEPARATOR);
		StringTokenizer urlTokenizer = new StringTokenizer(urls, WikiConstants.WIKISPACE_SEPARATOR);
		while (nameTokenizer.hasMoreTokens() && urlTokenizer.hasMoreTokens()) {
			String wiki = nameTokenizer.nextToken();
			String urlPrefix = urlTokenizer.nextToken();
			wikiSpace.put(wiki, urlPrefix);
		}
		return wikiSpace;
	}

	public static Map getWikiSpace() {
		return WikiPreferences.wikispace;
	}

	static void setWikiSpace(Map wikis) {
		WikiPreferences.wikispace = wikis;
	}
}