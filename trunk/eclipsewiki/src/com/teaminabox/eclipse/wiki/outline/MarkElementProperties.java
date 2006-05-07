package com.teaminabox.eclipse.wiki.outline;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public final class MarkElementProperties implements IPropertySource {

	protected MarkElement			element;

	protected static final String	PROPERTY_LINECOUNT	= "lineno"; //$NON-NLS-1$
	protected static final String	PROPERTY_START		= "start";	//$NON-NLS-1$
	protected static final String	PROPERTY_LENGTH		= "length"; //$NON-NLS-1$

	/**
	 * Creates a new MarkElementProperties.
	 * 
	 * @param element
	 *            the element whose properties this instance represents
	 */
	public MarkElementProperties(MarkElement element) {
		super();
		this.element = element;
	}

	/*
	 * (non-Javadoc) Method declared on IPropertySource
	 */
	public Object getEditableValue() {
		return this;
	}

	/*
	 * (non-Javadoc) Method declared on IPropertySource
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		// Create the property vector.
		IPropertyDescriptor[] propertyDescriptors = new IPropertyDescriptor[3];

		// Add each property supported.
		PropertyDescriptor descriptor;

		descriptor = new PropertyDescriptor(MarkElementProperties.PROPERTY_LINECOUNT, "Line_count"); //$NON-NLS-1$
		propertyDescriptors[0] = descriptor;
		descriptor = new PropertyDescriptor(MarkElementProperties.PROPERTY_START, "Title_start"); //$NON-NLS-1$
		propertyDescriptors[1] = descriptor;
		descriptor = new PropertyDescriptor(MarkElementProperties.PROPERTY_LENGTH, "Title_length"); //$NON-NLS-1$
		propertyDescriptors[2] = descriptor;

		// Return it.
		return propertyDescriptors;
	}

	/*
	 * (non-Javadoc) Method declared on IPropertySource
	 */
	public Object getPropertyValue(Object name) {
		if (name.equals(MarkElementProperties.PROPERTY_LINECOUNT)) {
			return new Integer(element.getNumberOfLines());
		}
		if (name.equals(MarkElementProperties.PROPERTY_START)) {
			return new Integer(element.getStart());
		}
		if (name.equals(MarkElementProperties.PROPERTY_LENGTH)) {
			return new Integer(element.getLength());
		}
		return null;
	}

	/*
	 * (non-Javadoc) Method declared on IPropertySource
	 */
	public boolean isPropertySet(Object property) {
		return false;
	}

	/*
	 * (non-Javadoc) Method declared on IPropertySource
	 */
	public void resetPropertyValue(Object property) {
	}

	/*
	 * (non-Javadoc) Method declared on IPropertySource
	 */
	public void setPropertyValue(Object name, Object value) {
	}

}