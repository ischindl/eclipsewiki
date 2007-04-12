package com.teaminabox.eclipse.wiki.editors;

import java.beans.PropertyChangeListener;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import com.teaminabox.eclipse.wiki.WikiPlugin;
import com.teaminabox.eclipse.wiki.properties.ProjectProperties;

public class PropertyAdapter implements PropertyChangeListener, IPropertyChangeListener {

	private final PropertyListener	listener;

	public PropertyAdapter(PropertyListener wikiEditor) {
		listener = wikiEditor;
		WikiPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);
		ProjectProperties.getInstance().addPropertyChangeListener(this);
	}

	public void dispose() {
		WikiPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(this);
		ProjectProperties.getInstance().removePropertyChangeListener(this);
	}

	public void propertyChange(PropertyChangeEvent event) {
		listener.propertyChanged();
	}

	public void propertyChange(java.beans.PropertyChangeEvent evt) {
		listener.propertyChanged();
	}

}