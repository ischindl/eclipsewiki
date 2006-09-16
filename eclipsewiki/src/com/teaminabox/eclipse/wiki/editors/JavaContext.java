package com.teaminabox.eclipse.wiki.editors;

import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import com.teaminabox.eclipse.wiki.WikiPlugin;

public final class JavaContext implements IResourceChangeListener {

	private final WikiDocumentContext	context;
	private IJavaProject				javaProject;
	private HashSet						packages;

	public JavaContext(WikiDocumentContext context) throws CoreException {
		this.context = context;
		initialiseJavaProject();
		listenToResourceChanges();
	}

	private void listenToResourceChanges() {
		if (isInJavaProject()) {
			ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
		}
	}

	private void initialiseJavaProject() throws CoreException {
		IProject project = context.getProject();
		if (project.hasNature(JavaCore.NATURE_ID)) {
			javaProject = JavaCore.create(project);
			loadPackages();
		}
	}

	public IJavaProject getJavaProject() {
		return javaProject;
	}

	private synchronized void loadPackages() throws JavaModelException {
		if (!isInJavaProject()) {
			return;
		}
		packages = new HashSet();
		IPackageFragment[] packageFragments = javaProject.getPackageFragments();
		for (int i = 0; i < packageFragments.length; i++) {
			if (!packageFragments[i].isDefaultPackage()) {
				packages.add(packageFragments[i].getElementName());
			}
		}
	}

	public boolean isInJavaProject() {
		return javaProject != null && javaProject.exists();
	}

	public synchronized boolean startsWithPackageName(String text) {
		if (!isInJavaProject()) {
			return false;
		}
		if (packages.contains(text)) {
			return true;
		}
		return hasPackageWithNameStartingWith(text);
	}

	private boolean hasPackageWithNameStartingWith(String text) {
		Iterator iterator = packages.iterator();
		while (iterator.hasNext()) {
			String packageName = (String) iterator.next();
			if (text.startsWith(packageName)) {
				return true;
			}
		}
		return false;
	}

	public void resourceChanged(IResourceChangeEvent event) {
		try {
			loadPackages();
		} catch (JavaModelException e) {
			WikiPlugin.getDefault().log("JavaContext", e);
		}
	}

	public void dispose() {
		if (isInJavaProject()) {
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		}
	}

}