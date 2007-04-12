package com.teaminabox.eclipse.wiki.properties;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class ProjectPropertiesTest {
	private final Mockery	context	= new JUnit4Mockery();
	private final IProject	project	= context.mock(IProject.class);

	@Before
	public void tearDown() throws CoreException {
		ProjectProperties.getInstance().removeListeners();
		context.checking(new Expectations() {
			{
				ignoring(project).getPersistentProperty(ProjectProperties.RENDERER);
			}
		});
	}

	@Test
	public void testClearRendererSetsPropertyToNull() throws Exception {
		context.checking(new Expectations() {
			{
				one(project).setPersistentProperty(ProjectProperties.RENDERER, null);
			}
		});
		ProjectProperties.getInstance().clearRenderer(project);
	}

	@Test
	public void testSetRenderer() throws CoreException {
		final PropertyChangeListener listener = context.mock(PropertyChangeListener.class);
		context.checking(new Expectations() {
			{
				one(listener).propertyChange(new PropertyChangeEvent(ProjectProperties.getInstance(), ProjectProperties.RENDERER.getLocalName(), null, "new"));
				one(project).setPersistentProperty(ProjectProperties.RENDERER, "new");
			}
		});
		ProjectProperties.getInstance().addPropertyChangeListener(listener);
		ProjectProperties.getInstance().setRenderer(project, "new");
	}

	@Test
	public void testPropertyChangeListenerNotCalledAfterItIsRemoved() throws CoreException {
		PropertyChangeListener listener = context.mock(PropertyChangeListener.class);
		context.checking(new Expectations() {
			{
				one(project).setPersistentProperty(ProjectProperties.RENDERER, "new");
			}
		});
		ProjectProperties.getInstance().addPropertyChangeListener(listener);
		ProjectProperties.getInstance().removePropertyChangeListener(listener);
		ProjectProperties.getInstance().setRenderer(project, "new");
	}
}
