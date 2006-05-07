package com.teaminabox.eclipse.wiki.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.WikiPlugin;

public final class EditorPreferences extends FieldEditor {

	private final WikiPreferences	preferences;

	public EditorPreferences(WikiPreferences preferences, Composite parent) {
		this.preferences = preferences;
		createControl(parent);
	}

	protected void adjustForNumColumns(int numColumns) {

	}

	protected void doFillIntoGrid(Composite parent, int numColumns) {
		Composite basicComposite = new Composite(parent, SWT.NONE);
		GridLayout basicLayout = new GridLayout();
		basicLayout.numColumns = numColumns;
		basicComposite.setLayout(basicLayout);

		preferences.addField(new ColourFieldWithDefaultEditor(basicComposite));
		preferences.addField(new MultipleColourFieldEditor(basicComposite));
		preferences.addField(new StringFieldEditor(WikiConstants.RESOURCE_WIKI_SYNTAX_PREFERENCE_PAGE_HOVER_LENGTH, WikiPlugin.getResourceString(WikiConstants.HOVER_PREVIEW_LENGTH), basicComposite));
		preferences.addField(new BooleanFieldEditor(WikiConstants.REUSE_EDITOR, WikiPlugin.getResourceString("WikiSyntaxPreferencePage.reuseEditor"), basicComposite));
		preferences.addField(new BooleanFieldEditor(WikiConstants.WORD_WRAP, WikiPlugin.getResourceString("WikiSyntaxPreferencePage.wordWrap"), basicComposite));
	}

	protected void doLoad() {

	}

	protected void doLoadDefault() {

	}

	protected void doStore() {

	}

	public int getNumberOfControls() {
		return 1;
	}

}