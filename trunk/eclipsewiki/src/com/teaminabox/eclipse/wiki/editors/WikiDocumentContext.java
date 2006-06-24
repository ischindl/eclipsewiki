package com.teaminabox.eclipse.wiki.editors;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.WikiPlugin;
import com.teaminabox.eclipse.wiki.preferences.WikiPreferences;
import com.teaminabox.eclipse.wiki.text.WikiLinkTextRegion;

/**
 * The local context a Wiki document lives in.
 * <p>
 * I take care of the things relating to the file system, launching etc rather than leaving all that in the editor. It
 * also means that wiki files can be processed independently of an editor.
 */
public final class WikiDocumentContext {

	private static final String	DEFAULT_CHARSET	= "UTF8";
	private final IFile			wikiDocument;
	private Properties			localWikispace;
	private HashSet				excludeList;
	private JavaContext			javaContext;

	public WikiDocumentContext(IFile wikiDocument) {
		this.wikiDocument = wikiDocument;
		loadEnvironment();
		javaContext = new JavaContext(this);
	}

	public JavaContext getJavaContext() {
		return javaContext;
	}

	public void loadEnvironment() {
		loadLocalWikiSpace();
		loadExcludes();
	}

	private void loadLocalWikiSpace() {
		try {
			localWikispace = new Properties();
			IContainer container = getWorkingLocation();
			Path path = new Path(WikiConstants.WIKISPACE_FILE);
			IFile file = container.getFile(path);
			if (file.exists() && !file.isPhantom()) {
				localWikispace.load(new BufferedInputStream(file.getContents()));
			}
		} catch (Exception e) {
			WikiPlugin.getDefault().logAndReport(WikiPlugin.getResourceString(WikiConstants.RESOURCE_WIKI_ERROR_DIALOGUE_PROGRAMMATIC_ERROR_TITLE), WikiPlugin.getResourceString(WikiConstants.RESOURCE_WIKI_ERROR_DIALOGUE_PROGRAMMATIC_ERROR_TEXT), e);
		}
	}

	private void loadExcludes() {
		try {
			excludeList = new HashSet();
			IContainer container = getWorkingLocation();
			Path path = new Path(WikiConstants.EXCLUDES_FILE);
			IFile file = container.getFile(path);
			if (file.exists() && !file.isPhantom()) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(file.getContents(), getCharset()));
				String line;
				while ((line = reader.readLine()) != null) {
					if (line.trim().length() > 0) {
						excludeList.add(line.trim());
					}
				}
				reader.close();
			}
		} catch (Exception e) {
			WikiPlugin.getDefault().logAndReport(WikiPlugin.getResourceString(WikiConstants.RESOURCE_WIKI_ERROR_DIALOGUE_PROGRAMMATIC_ERROR_TITLE), WikiPlugin.getResourceString(WikiConstants.RESOURCE_WIKI_ERROR_DIALOGUE_PROGRAMMATIC_ERROR_TEXT), e);
		}
	}

	public Charset getCharset() {
		try {
			return Charset.forName(wikiDocument.getCharset());
		} catch (CoreException e) {
			e.printStackTrace();
			return Charset.forName(DEFAULT_CHARSET);
		}
	}

	public IContainer getWorkingLocation() {
		return wikiDocument.getParent();
	}

	public IProject getProject() {
		return getWorkingLocation().getProject();
	}

	public IFile getFileForWikiName(String wikiName) {
		IContainer container = getWorkingLocation();
		if (container == null) {
			return null;
		}
		IResource resource = container.findMember(wikiName + WikiConstants.WIKI_FILE_EXTENSION);
		if (resource == null) {
			return null;
		}
		return (IFile) resource;
	}
	
	public String loadContents(IFile file) throws CoreException, IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getContents()));
		StringBuffer buffer = new StringBuffer();
		int c;
		while ((c = bufferedReader.read()) != -1) {
			buffer.append((char) c);
		}
		return buffer.toString();
	}

	IFile getWikiFile(String word) {
		IContainer container = getWorkingLocation();
		String wikiFileName = word + WikiConstants.WIKI_FILE_EXTENSION;
		Path path = new Path(wikiFileName);
		return container.getFile(path);
	}

	void createNewFileIfNeeded(IFile file) throws CoreException {
		if (!file.exists()) {
			createWikiFile(file);
		}
	}

	private void createWikiFile(IFile file) throws CoreException {
		String newText = WikiPlugin.getResourceString(WikiConstants.RESOURCE_NEW_PAGE_HEADER) + " " + getWikiNameBeingEdited();
		byte[] buffer = newText.getBytes();
		ByteArrayInputStream source = new ByteArrayInputStream(buffer);
		file.create(source, true, new NullProgressMonitor());
	}

	public String getWikiNameBeingEdited() {
		String fileName = wikiDocument.getName();
		return new String(fileName.substring(0, fileName.indexOf(WikiConstants.WIKI_FILE_EXTENSION)));
	}

	public boolean hasWikiSibling(WikiLinkTextRegion wikiNameTextRegion) {
		IFile file = getWikiFile(wikiNameTextRegion.getWikiDocumentName());
		return file.exists();
	}

	public String getWikiSpaceLink(String name) {
		return (String) getWikiSpace().get(name);
	}

	public Map getWikiSpace() {
		HashMap local = new HashMap();
		local.putAll(WikiPreferences.getWikiSpace());
		local.putAll(localWikispace);
		return local;
	}

	public String[] getDocument() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(wikiDocument.getContents()));
			String line;
			ArrayList lines = new ArrayList();
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
			return (String[]) lines.toArray(new String[lines.size()]);
		} catch (Exception e) {
			WikiPlugin.getDefault().log("Cannot get Document", e);
			return new String[] { "Unable to load document - please check the logs." };
		}
	}

	public boolean isExcluded(String word) {
		return excludeList.contains(word);
	}

	public HashSet getExcludeSet() {
		return excludeList;
	}

	public void dispose() {
		javaContext.dispose();
	}
}
