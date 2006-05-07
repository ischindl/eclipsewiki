package com.teaminabox.eclipse.wiki.preferences;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.WikiPlugin;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	private IPreferenceStore	store;

	public PreferenceInitializer() {
		store = WikiPlugin.getDefault().getPreferenceStore();
	}

	public void initializeDefaultPreferences() {
		PreferenceConverter.setDefault(store, AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND, Display.getDefault().getSystemColor(SWT.COLOR_LIST_FOREGROUND).getRGB());
		store.setDefault(AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND_SYSTEM_DEFAULT, true);
		PreferenceConverter.setDefault(store, AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND, Display.getDefault().getSystemColor(SWT.COLOR_LIST_BACKGROUND).getRGB());
		store.setDefault(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND_SYSTEM_DEFAULT, true);

		setDefault(store, WikiConstants.WIKI_NAME, WikiConstants.WIKI_NAME_DEFAULT_COLOUR, WikiConstants.STYLE_BOLD);
		setDefault(store, WikiConstants.NEW_WIKI_NAME, WikiConstants.NEW_WIKI_NAME_DEFAULT_COLOUR, WikiConstants.STYLE_NORMAL);
		setDefault(store, WikiConstants.WIKI_URL, WikiConstants.WIKI_URL_DEFAULT_COLOUR, WikiConstants.STYLE_BOLD);
		setDefault(store, WikiConstants.URL, WikiConstants.URL_DEFAULT_COLOUR, WikiConstants.STYLE_BOLD);
		setDefault(store, WikiConstants.ECLIPSE_RESOURCE, WikiConstants.ECLIPSE_RESOURCE_DEFAULT_COLOUR, WikiConstants.STYLE_NORMAL);
		setDefault(store, WikiConstants.PLUGIN_RESOURCE, WikiConstants.ECLIPSE_RESOURCE_DEFAULT_COLOUR, WikiConstants.STYLE_NORMAL);
		setDefault(store, WikiConstants.JAVA_TYPE, WikiConstants.JAVA_TYPE_DEFAULT_COLOUR, WikiConstants.STYLE_NORMAL);
		setDefault(store, WikiConstants.OTHER, WikiConstants.OTHER_DEFAULT_COLOUR, WikiConstants.STYLE_NORMAL);

		store.setDefault(WikiConstants.HOVER_PREVIEW_LENGTH, WikiConstants.DEFAULT_HOVER_PREVIEW_LENGTH);

		store.setDefault(WikiConstants.REUSE_EDITOR, WikiConstants.DEFAULT_REUSE_EDITOR);
		store.setDefault(WikiConstants.WORD_WRAP, WikiConstants.DEFAULT_WORD_WRAP);
		store.setDefault(WikiConstants.BROWSER_RENDERER, WikiConstants.BROWSER_RENDERERS[0]);

		store.setDefault(WikiConstants.RENDER_FULLY_QUALIFIED_TYPE_NAMES, WikiConstants.DEFAULT_RENDER_FULLY_QUALIFIED_TYPE_NAMES);

		loadWikiSpaceDefaults(store);
	}

	private void loadWikiSpaceDefaults(IPreferenceStore store) {
		try {
			Properties properties = new Properties();
			properties.load(FileLocator.openStream(WikiPlugin.getDefault().getBundle(), new Path(WikiConstants.WIKISPACE_FILE), false));
			StringBuffer names = new StringBuffer();
			StringBuffer urls = new StringBuffer();
			Iterator iterator = properties.keySet().iterator();
			while (iterator.hasNext()) {
				String name = (String) iterator.next();
				String url = properties.getProperty(name);
				names.append(name).append(WikiConstants.WIKISPACE_SEPARATOR);
				urls.append(url).append(WikiConstants.WIKISPACE_SEPARATOR);
			}
			store.setDefault(WikiConstants.WIKISPACE_NAMES, names.toString());
			store.setDefault(WikiConstants.WIKISPACE_URLS, urls.toString());
		} catch (IOException e) {
			WikiPlugin.getDefault().log("", e);
		}
	}

	private void setDefault(IPreferenceStore store, String constant, String color, String style) {
		store.setDefault(constant + WikiConstants.SUFFIX_FOREGROUND, color);
		store.setDefault(constant + WikiConstants.SUFFIX_STYLE, style);
	}

}
