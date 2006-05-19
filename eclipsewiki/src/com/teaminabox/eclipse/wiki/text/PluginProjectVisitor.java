/*
 * Created on 12.11.2004
 *
 */
package com.teaminabox.eclipse.wiki.text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

/**
 * @author Ronald Steinhau
 */
public final class PluginProjectVisitor extends ProjectVisitor {
	private static Map	gProjects;

	public static IProject locateProjectInWorkspace(String pluginID) {
		IWorkspaceRoot rootFolder = ResourcesPlugin.getWorkspace().getRoot();
		List projList = new ArrayList(1);
		try {
			PluginProjectVisitor ppVisitor = new PluginProjectVisitor(pluginID);
			rootFolder.accept(ppVisitor);
			projList = ppVisitor.getProjectsFound();
		} catch (CoreException e) {
			throw new RuntimeException("error locating workspace folder", e);
		}
		if (projList.size() > 0) {
			IProject proj = (IProject) projList.get(0);
			return proj;
		}
		return null;
	}

	public static String extractPlugID(IProject proj) {
		if (!proj.isAccessible() || !proj.isOpen()) {
			return null;
		}
		if (PluginProjectVisitor.gProjects == null) {
			PluginProjectVisitor.gProjects = new HashMap();
		}
		String id = (String) gProjects.get(proj.getName());
		if (id == null) {
			IResource member = proj.getFile("plugin.xml");
			String type = "plugin";
			if (member == null || !member.exists()) {
				member = proj.getFile("fragment.xml");
				type = "fragment";
			}
			if (member != null && member.exists()) {
				try {
					String content = loadContents(member);
					int pos = content.indexOf("<" + type);
					if (pos > 0) {
						int pos2 = content.indexOf(">", pos);
						StringBuffer header = new StringBuffer();
						for (int i = pos; i < pos2; i++) {
							if (content.charAt(i) != ' ') {
								header.append(content.charAt(i));
							}
						}
						content = header.toString();
						pos = content.indexOf("id=");
						pos = content.indexOf("\"", pos);
						pos2 = content.indexOf("\"", pos + 1);
						id = content.substring(pos + 1, pos2).trim();
						gProjects.put(proj.getName(), id);
					}
				} catch (Exception e) {
					return null;
				}
			}
		}
		return id;
	}

	private static String loadContents(IResource member) throws CoreException, IOException {
		IFile pluginFile = (IFile) member;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buf = new byte[10000];
		InputStream in = pluginFile.getContents();
		while (in.available() > 0) {
			int len = in.available();
			if (len > buf.length) {
				len = buf.length;
			}
			in.read(buf, 0, len);
			bos.write(buf, 0, len);
		}
		in.close();
		bos.close();

		String content = bos.toString();
		return content;
	}

	// ------------------------------------------------------------

	private String	fPluginID;

	public PluginProjectVisitor(String pluginID) {
		super();
		fPluginID = pluginID;
	}

	protected boolean isValidProject(IProject proj) {
		if (!proj.isAccessible() || !proj.isOpen()) {
			return false;
		}
		String projPlugID = PluginProjectVisitor.extractPlugID(proj);
		if (projPlugID == null) {
			return false;
		}
		if (fPluginID == null) {
			return true;
		}
		return fPluginID.equals(projPlugID);
	}
}