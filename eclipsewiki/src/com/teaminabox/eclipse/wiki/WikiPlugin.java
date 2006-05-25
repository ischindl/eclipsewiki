package com.teaminabox.eclipse.wiki;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.teaminabox.eclipse.wiki.editors.WikiEditor;
import com.teaminabox.eclipse.wiki.preferences.WikiPreferences;

public final class WikiPlugin extends AbstractUIPlugin {

	public static final String	RESOURCE_BUNDLE	= "com.teaminabox.eclipse.wiki.WikiPluginResources";
	private Set					editors;

	private static WikiPlugin	plugin;
	private ResourceBundle		resourceBundle;

	public WikiPlugin() {
		WikiPlugin.plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		initialiseResourceBundle();
		initialiseImageRegistry();
		WikiPreferences.init(getPreferenceStore());
		editors = new HashSet();
		addResourceChangeListener();
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		WikiPlugin.plugin = null;
		resourceBundle = null;
	}

	private void initialiseImageRegistry() {
		for (int i = 0; i < WikiConstants.ICONS.length; i++) {
			getImageRegistry().put(WikiConstants.ICONS[i], AbstractUIPlugin.imageDescriptorFromPlugin(WikiConstants.PLUGIN_ID, WikiConstants.ICONS[i]));
		}
	}

	private void initialiseResourceBundle() {
		try {
			resourceBundle = ResourceBundle.getBundle(WikiPlugin.RESOURCE_BUNDLE);
		} catch (MissingResourceException e) {
			logAndReport("Error", "The Resource Bundle is missing!!", e);
		}
	}

	public String loadTextContents(IPath path) throws IOException {
		InputStream stream = FileLocator.openStream(getBundle(), path, false);
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
		StringBuffer buffer = new StringBuffer();
		int c;
		while ((c = bufferedReader.read()) != -1) {
			buffer.append((char) c);
		}
		return buffer.toString();
	}

	public static WikiPlugin getDefault() {
		return WikiPlugin.plugin;
	}

	public static String getResourceString(String key) {
		ResourceBundle bundle = WikiPlugin.getDefault().getResourceBundle();
		try {
			return bundle.getString(key);
		} catch (MissingResourceException e) {
			return "!" + key + "!";
		}
	}

	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	public void logAndReport(String title, String message, Exception e) {
		log(message, e);
		reportError(title, message);
	}

	public void log(String message) {
		getDefault().getLog().log(new Status(IStatus.OK, WikiConstants.PLUGIN_ID, IStatus.OK, message, null));
	}

	public void log(String message, Exception e) {
		if (message == null) {
			message = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : e.getClass().getName();
		}
		getDefault().getLog().log(new Status(IStatus.ERROR, WikiConstants.PLUGIN_ID, IStatus.OK, message, e));
	}

	public void reportError(String title, String message) {
		try {
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			MessageDialog.openError(shell, title, message);
		} catch (RuntimeException e) {
			log(e.getLocalizedMessage(), e);
		}
	}

	public void registerEditor(WikiEditor editor) {
		editors.add(editor);
	}

	public void unregisterEditor(WikiEditor editor) {
		editors.remove(editor);
	}

	public WikiEditor[] getEditors() {
		return (WikiEditor[]) editors.toArray(new WikiEditor[editors.size()]);
	}

	private void addResourceChangeListener() {
		IResourceChangeListener resourceChangeListener = new IResourceChangeListener() {
			public void resourceChanged(IResourceChangeEvent event) {
				WikiPlugin.this.processResourceChange(event);
			}
		};
		ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceChangeListener);
	}

	private void processResourceChange(IResourceChangeEvent event) {
		try {
			if (event.getType() != IResourceChangeEvent.POST_CHANGE) {
				return;
			}
			Iterator iterator = editors.iterator();
			while (iterator.hasNext()) {
				WikiEditor editor = (WikiEditor) iterator.next();
				if (editor.getContext() != null) {
					editor.getContext().loadEnvironment();
					editor.redrawTextAsync();
				}
			}
		} catch (RuntimeException e) {
			log("WikiEditor: Resource Change Error", e);
		}
	}
}