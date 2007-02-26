package com.teaminabox.eclipse.wiki.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.WikiPlugin;

public final class Resources {

	public static String getContentsRelativeToPlugin(IPath path) throws IOException {
		return Resources.getContents(FileLocator.openStream(WikiPlugin.getDefault().getBundle(), path, false));
	}

	public static String getContents(InputStream stream) throws IOException {
		InputStreamReader inputStreamReader = new InputStreamReader(stream);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		StringBuffer buffer = new StringBuffer(1000);
		int c;
		while ((c = bufferedReader.read()) != -1) {
			buffer.append((char) c);
		}
		return buffer.toString();
	}

	public static String getContents(IFile file) throws IOException, CoreException {
		return Resources.getContents(file.getContents());
	}

	public static List<String> readLines(IFile file) throws IOException, CoreException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(file.getContents()));
		String line;
		ArrayList<String> lines = new ArrayList<String>();
		while ((line = reader.readLine()) != null) {
			lines.add(line);
		}
		return lines;
	}

	public static boolean exists(IResource resource) {
		return resource != null && resource.exists();
	}

	public static boolean existsAsFile(IResource resource) {
		return Resources.exists(resource) && resource.getType() == IResource.FILE;
	}

	public static IFile findFileInWorkspace(String workspaceRelativePath) {
		IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(workspaceRelativePath);
		if (Resources.existsAsFile(resource)) {
			return (IFile) resource;
		}
		return null;
	}

	public static boolean isWikiFile(IResource resource) {
		return Resources.exists(resource) && resource.getFileExtension() != null && WikiConstants.WIKI_FILE_EXTENSION.endsWith(resource.getFileExtension());
	}

	public static boolean isWikiFile(IFile file) {
		return file.getName().endsWith(WikiConstants.WIKI_FILE_EXTENSION);
	}
}
