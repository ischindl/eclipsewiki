package com.teaminabox.eclipse.wiki.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;

import com.teaminabox.eclipse.wiki.WikiPlugin;

public class Resources {

	public static String getContentsRelativeToPlugin(IPath path) throws IOException {
		return getContents(FileLocator.openStream(WikiPlugin.getDefault().getBundle(), path, false));
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
	
	public static List readLines(IFile file) throws IOException, CoreException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(file.getContents()));
		String line;
		ArrayList lines = new ArrayList();
		while ((line = reader.readLine()) != null) {
			lines.add(line);
		}
		return lines;
	}
	
	public static boolean exists(IResource resource) {
		return resource != null && resource.exists();
	}
}
