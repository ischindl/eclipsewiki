package com.teaminabox.eclipse.wiki.editors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import com.teaminabox.eclipse.wiki.WikiTest;

public class JavaContextTest extends WikiTest {

	private static final String	CLASS_SOURCE			= "package com.teaminabox.foo;\npublic class BigClass { class InnerClass {} }";
	private static final String	CLASS_NAME				= "com.teaminabox.foo.BigClass";

	private static final String	ANOTHER_CLASS_SOURCE	= "package net.sourceforge;\npublic class Project { class InnerClass {} }";
	private static final String	ANOTHER_CLASS_NAME		= "net.sourceforge.Project";

	public JavaContextTest(String name) {
		super(name);
	}

	public void testGetJavaProjectInNonJavaProject() throws CoreException {
		IProject project = createNonJavaProject("simpleProject");
		IFile file = createFile(project, "test.txt", "Hi");
		WikiDocumentContext wikiDocumentContext = new WikiDocumentContext(file);
		JavaContext javaContext = new JavaContext(wikiDocumentContext);
		assertFalse(javaContext.isInJavaProject());
		project.delete(true, true, null);
	}

	public void testIsInJavaProject() throws CoreException {
		IFile file = createFile(getJavaProject().getProject(), "test.txt", "Hi");
		WikiDocumentContext wikiDocumentContext = new WikiDocumentContext(file);
		JavaContext javaContext = wikiDocumentContext.getJavaContext();
		assertTrue(javaContext.isInJavaProject());
	}

	public void testStartsWithPackageNameForPartialMatch() throws CoreException {
		create(JavaContextTest.CLASS_SOURCE, JavaContextTest.CLASS_NAME.replaceAll("\\.", "/") + ".java");
		IFile file = createFile(getJavaProject().getProject(), "test.txt", "Hi");
		WikiDocumentContext wikiDocumentContext = new WikiDocumentContext(file);
		assertTrue(wikiDocumentContext.getJavaContext().startsWithPackageName("com...blah"));
	}

	public void testStartsWithPackageNameForFullyQualifiedName() throws CoreException {
		create(JavaContextTest.CLASS_SOURCE, JavaContextTest.CLASS_NAME.replaceAll("\\.", "/") + ".java");
		IFile file = createFile(getJavaProject().getProject(), "test.txt", "Hi");
		WikiDocumentContext wikiDocumentContext = new WikiDocumentContext(file);
		assertTrue(wikiDocumentContext.getJavaContext().startsWithPackageName(JavaContextTest.CLASS_NAME));
	}

	public void testStartsWithPackageNameWhenFalse() throws CoreException {
		create(JavaContextTest.CLASS_SOURCE, JavaContextTest.CLASS_NAME.replaceAll("\\.", "/") + ".java");
		IFile file = createFile(getJavaProject().getProject(), "test.txt", "Hi");
		WikiDocumentContext wikiDocumentContext = new WikiDocumentContext(file);
		assertFalse(wikiDocumentContext.getJavaContext().startsWithPackageName("something else"));
	}

	public void testStartsWithPackageNameWhenNewPackageAdded() throws CoreException {
		create(JavaContextTest.CLASS_SOURCE, JavaContextTest.CLASS_NAME.replaceAll("\\.", "/") + ".java");
		IFile file = createFile(getJavaProject().getProject(), "test.txt", "Hi");
		WikiDocumentContext wikiDocumentContext = new WikiDocumentContext(file);

		create(JavaContextTest.ANOTHER_CLASS_SOURCE, JavaContextTest.ANOTHER_CLASS_NAME.replaceAll("\\.", "/") + ".java");
		assertTrue(wikiDocumentContext.getJavaContext().startsWithPackageName(JavaContextTest.ANOTHER_CLASS_NAME));
	}

}
