package com.teaminabox.eclipse.wiki.properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PropertyPage;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.WikiPlugin;

public class PropertiesPage extends PropertyPage {

	private Combo	combo;
	private Button	enabled;

	public PropertiesPage() {
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(1, false);
		composite.setLayout(layout);

		enabled = new Button(composite, SWT.CHECK);
		enabled.setText("Enable Project Specific Settings: ");

		enabled.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				toggleEnablement();
			}

		});
		enabled.setSelection(ProjectProperties.getInstance().isProjectPropertiesEnabled(getProject()));

		Label label = new Label(composite, SWT.LEFT);
		label.setText("Renderer: ");

		combo = new Combo(composite, SWT.DROP_DOWN);
		setRendererLabels();

		Dialog.applyDialogFont(composite);

		return composite;
	}

	protected void toggleEnablement() {
		combo.setEnabled(enabled.getSelection());
	}

	private void setRendererLabels() {
		String preferred = ProjectProperties.getInstance().getRenderer(getProject());
		int selected = 0;
		String[] items = new String[WikiConstants.BROWSER_RENDERERS.length];
		for (int i = 0; i < WikiConstants.BROWSER_RENDERERS.length; i++) {
			String renderer = WikiConstants.BROWSER_RENDERERS[i];
			items[i] = WikiPlugin.getResourceString(renderer);
			if (preferred.equals(renderer)) {
				selected = i;
			}
		}
		combo.setItems(items);
		combo.select(selected);
	}

	@Override
	protected void performDefaults() {
		ProjectProperties.getInstance().setDefaults(getProject());
	}

	@Override
	public boolean performOk() {
		ProjectProperties.getInstance().setRenderer(getProject(), WikiConstants.BROWSER_RENDERERS[combo.getSelectionIndex()]);
		ProjectProperties.getInstance().setProjectPropertiesEnabled(getProject(), enabled.getSelection());
		return true;
	}

	private IProject getProject() {
		if (getElement() instanceof IJavaProject) {
			return ((IJavaProject) getElement()).getProject();
		}
		return (IProject) getElement();
	}

}
