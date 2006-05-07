package com.teaminabox.eclipse.wiki.outline;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.views.properties.IPropertySource;

/**
 * This represents a node in the outline view.
 */
public final class MarkElement implements IWorkbenchAdapter, IAdaptable {

	private String			headingName;
	private int				offset;
	private int				numberOfLines;
	private int				length;
	private ArrayList		children;
	private ImageDescriptor	imageDescriptor;

	/**
	 * either the parent element (MarkElement) or the manifest file (IFile)
	 */
	private IAdaptable		parent;

	public MarkElement(IAdaptable parent, String heading, int offset, int length, ImageDescriptor imageDescriptor) {
		this.parent = parent;
		this.imageDescriptor = imageDescriptor;
		if (parent instanceof MarkElement) {
			((MarkElement) parent).addChild(this);
		}
		this.headingName = heading;
		this.offset = offset;
		this.length = length;
		children = new ArrayList();
	}

	public IFile getManifestFile() {
		if (parent instanceof MarkElement) {
			return ((MarkElement) parent).getManifestFile();
		}
		return (IFile) parent;
	}

	private void addChild(MarkElement child) {
		children.add(child);
	}

	/*
	 * (non-Javadoc) Method declared on IAdaptable
	 */
	public Object getAdapter(Class adapter) {
		if (adapter == IWorkbenchAdapter.class) {
			return this;
		}
		if (adapter == IPropertySource.class) {
			return new MarkElementProperties(this);
		}

		return null;
	}

	/*
	 * (non-Javadoc) Method declared on IWorkbenchAdapter
	 */
	public Object[] getChildren(Object object) {
		if (children != null) {
			return children.toArray();
		}
		return new Object[0];
	}

	/*
	 * (non-Javadoc) Method declared on IWorkbenchAdapter
	 */
	public ImageDescriptor getImageDescriptor(Object object) {
		return imageDescriptor;
	}

	/*
	 * (non-Javadoc) Method declared on IWorkbenchAdapter
	 */
	public String getLabel(Object o) {
		return headingName;
	}

	/**
	 * Returns the number of characters in this section.
	 */
	public int getLength() {
		return length;
	}

	/**
	 * Returns the number of lines in the element.
	 * 
	 * @return the number of lines in the element
	 */
	public int getNumberOfLines() {
		return numberOfLines;
	}

	/*
	 * (non-Javadoc) Method declared on IWorkbenchAdapter
	 */
	public Object getParent(Object o) {
		return null;
	}

	/**
	 * Returns the offset of this section in the file.
	 */
	public int getStart() {
		return offset;
	}

	/**
	 * Sets the number of lines in the element
	 * 
	 * @param newNumberOfLines
	 *            the number of lines in the element
	 */
	public void setNumberOfLines(int newNumberOfLines) {
		numberOfLines = newNumberOfLines;
	}

}