package com.teaminabox.eclipse.wiki.preferences;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import com.teaminabox.eclipse.wiki.WikiPlugin;

public class ColourFieldWithDefaultEditor extends FieldEditor {

	private Button		bgDefault;
	private ColorEditor	bgColorEditor;
	private Button		bgCustom;
	private Composite	colorComposite;

	public ColourFieldWithDefaultEditor(Composite parent) {
		createControl(parent);
	}

	protected void adjustForNumColumns(int numColumns) {
		((GridData) colorComposite.getLayoutData()).horizontalSpan = numColumns - 1;
	}

	protected void doFillIntoGrid(Composite parent, int numColumns) {
		colorComposite = new Composite(parent, SWT.NULL);
		GridData gd = new GridData();
		gd.horizontalSpan = numColumns - 1;
		colorComposite.setLayoutData(gd);

		Group backgroundComposite = new Group(colorComposite, SWT.SHADOW_ETCHED_IN);
		backgroundComposite.setLayout(new RowLayout());
		backgroundComposite.setText(WikiPlugin.getResourceString("WikiSyntaxPreferencePage.backgroundColor")); //$NON-NLS-1$

		SelectionAdapter backgroundSelectionListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				boolean custom = bgCustom.getSelection();
				bgColorEditor.getButton().setEnabled(custom);
			}
		};

		bgDefault = new Button(backgroundComposite, SWT.RADIO | SWT.LEFT);
		bgDefault.setText(WikiPlugin.getResourceString("WikiSyntaxPreferencePage.systemDefault")); //$NON-NLS-1$
		bgDefault.addSelectionListener(backgroundSelectionListener);

		bgCustom = new Button(backgroundComposite, SWT.RADIO | SWT.LEFT);
		bgCustom.setText(WikiPlugin.getResourceString("WikiSyntaxPreferencePage.custom")); //$NON-NLS-1$
		bgCustom.addSelectionListener(backgroundSelectionListener);

		bgColorEditor = new ColorEditor(backgroundComposite);

	}

	protected void doLoad() {
		setSelection(getPreferenceStore().getBoolean(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND_SYSTEM_DEFAULT));
	}

	private void setSelection(boolean systemDefault) {
		RGB rgb = PreferenceConverter.getColor(getPreferenceStore(), AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND);
		bgColorEditor.setColorValue(rgb);
		bgDefault.setSelection(systemDefault);
		bgCustom.setSelection(!systemDefault);
		bgColorEditor.getButton().setEnabled(!systemDefault);
	}

	protected void doLoadDefault() {
		setSelection(getPreferenceStore().getDefaultBoolean(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND_SYSTEM_DEFAULT));
	}

	protected void doStore() {
		PreferenceConverter.setValue(getPreferenceStore(), AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND, bgColorEditor.getColorValue());
		getPreferenceStore().setValue(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND_SYSTEM_DEFAULT, bgDefault.getSelection());
	}

	public int getNumberOfControls() {
		return 1;
	}

}
