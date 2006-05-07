package com.teaminabox.eclipse.wiki.preferences;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.WikiPlugin;

public class MultipleColourFieldEditor extends FieldEditor {

	private final String[][]	COLOUR_LIST_MODEL	= new String[][] { { WikiPlugin.getResourceString("WikiSyntaxPreferencePage.WikiName"), WikiConstants.WIKI_NAME }, //$NON-NLS-1$
			{ WikiPlugin.getResourceString("WikiSyntaxPreferencePage.NewWikiName"), WikiConstants.NEW_WIKI_NAME }, //$NON-NLS-1$
			{ WikiPlugin.getResourceString("WikiSyntaxPreferencePage.WikiSpaceURL"), WikiConstants.WIKI_URL }, //$NON-NLS-1$
			{ WikiPlugin.getResourceString("WikiSyntaxPreferencePage.URL"), WikiConstants.URL }, //$NON-NLS-1$
			{ WikiPlugin.getResourceString("WikiSyntaxPreferencePage.EclipseResource"), WikiConstants.ECLIPSE_RESOURCE }, //$NON-NLS-1$
			{ WikiPlugin.getResourceString("WikiSyntaxPreferencePage.PluginResource"), WikiConstants.PLUGIN_RESOURCE }, //$NON-NLS-1$
			{ WikiPlugin.getResourceString("WikiSyntaxPreferencePage.JavaType"), WikiConstants.JAVA_TYPE }, //$NON-NLS-1$
			{ WikiPlugin.getResourceString("WikiSyntaxPreferencePage.Other"), WikiConstants.OTHER }, //$NON-NLS-1$
													};

	private List				colors;
	private ColorEditor			fgColorEditor;
	private Button				fgBold;
	private RGB[]				currentColours		= new RGB[COLOUR_LIST_MODEL.length];
	private HashMap				currentBold			= new HashMap();

	public MultipleColourFieldEditor(Composite parent) {
		createControl(parent);
	}

	protected void adjustForNumColumns(int numColumns) {
		// TODO Auto-generated method stub

	}

	protected void doFillIntoGrid(Composite parent, int numColumns) {
		Composite colorComposite = new Composite(parent, SWT.NULL);
		colorComposite.setLayout(new GridLayout());
		colorComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label label = new Label(colorComposite, SWT.LEFT);
		label.setText(WikiPlugin.getResourceString("WikiSyntaxPreferencePage.foreground")); //$NON-NLS-1$
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Composite editorComposite = new Composite(colorComposite, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		editorComposite.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_BOTH);
		editorComposite.setLayoutData(gd);

		colors = new List(editorComposite, SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
		gd = new GridData(GridData.FILL_BOTH);
		colors.setLayoutData(gd);

		Composite stylesComposite = new Composite(editorComposite, SWT.NONE);
		layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 2;
		stylesComposite.setLayout(layout);
		stylesComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		label = new Label(stylesComposite, SWT.LEFT);
		label.setText(WikiPlugin.getResourceString("WikiSyntaxPreferencePage.color")); //$NON-NLS-1$
		gd = new GridData();
		gd.horizontalAlignment = GridData.BEGINNING;
		label.setLayoutData(gd);

		fgColorEditor = new ColorEditor(stylesComposite);

		Button fgColorButton = fgColorEditor.getButton();
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalAlignment = GridData.BEGINNING;
		fgColorButton.setLayoutData(gd);

		label = new Label(stylesComposite, SWT.LEFT);
		label.setText(WikiPlugin.getResourceString("WikiSyntaxPreferencePage.bold")); //$NON-NLS-1$
		gd = new GridData();
		gd.horizontalAlignment = GridData.BEGINNING;
		label.setLayoutData(gd);

		fgBold = new Button(stylesComposite, SWT.CHECK);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalAlignment = GridData.BEGINNING;
		fgBold.setLayoutData(gd);

		fgColorButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int i = colors.getSelectionIndex();
				currentColours[i] = fgColorEditor.getColorValue();
			}
		});

		fgBold.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int i = colors.getSelectionIndex();
				String key = COLOUR_LIST_MODEL[i][1] + WikiConstants.SUFFIX_STYLE;
				String value = (fgBold.getSelection()) ? WikiConstants.STYLE_BOLD : WikiConstants.STYLE_NORMAL;
				currentBold.put(key, value);
			}
		});
	}

	protected void doLoad() {
		for (int i = 0; i < COLOUR_LIST_MODEL.length; i++) {
			colors.add(COLOUR_LIST_MODEL[i][0]);
			currentColours[i] = PreferenceConverter.getColor(getPreferenceStore(), COLOUR_LIST_MODEL[i][1]);
		}
		colors.select(0);
	}

	protected void doLoadDefault() {
		for (int i = 0; i < COLOUR_LIST_MODEL.length; i++) {
			colors.add(COLOUR_LIST_MODEL[i][0]);
			currentColours[i] = PreferenceConverter.getDefaultColor(getPreferenceStore(), COLOUR_LIST_MODEL[i][1]);
		}
		colors.select(0);
	}

	protected void doStore() {
		for (int i = 0; i < currentColours.length; i++) {
			String key = COLOUR_LIST_MODEL[i][1] + WikiConstants.SUFFIX_FOREGROUND;
			PreferenceConverter.setValue(getPreferenceStore(), key, currentColours[i]);
		}
		for (Iterator iter = currentBold.keySet().iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			getPreferenceStore().setValue(key, (String) currentBold.get(key));
		}
	}

	public int getNumberOfControls() {
		return 1;
	}

}
