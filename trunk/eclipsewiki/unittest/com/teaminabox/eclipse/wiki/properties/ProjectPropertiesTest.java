package com.teaminabox.eclipse.wiki.properties;

import static com.teaminabox.eclipse.wiki.properties.ProjectProperties.projectProperties;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.teaminabox.eclipse.wiki.WikiConstants;

@RunWith(JMock.class)
public class ProjectPropertiesTest {

	private final Mockery	context	= new JUnit4Mockery();
	private final IProject	project	= context.mock(IProject.class);

	@Before
	public void tearDown() {
		projectProperties().removeListeners();
	}

	@Test
	public void testClearRendererSetsPropertyToNull() throws Exception {
		context.checking(new Expectations() {
			{
				ignoring(project).getPersistentProperty(ProjectProperties.RENDERER);
				will(returnValue(WikiConstants.DEFAULT_BROWSER_RENDERER));
				one(project).setPersistentProperty(ProjectProperties.RENDERER, null);
			}
		});
		projectProperties().clearRenderer(project);
	}

	@Test
	public void testSetRenderer() throws CoreException {
		final PropertyChangeListener listener = context.mock(PropertyChangeListener.class);
		context.checking(new Expectations() {
			{
				ignoring(project).getPersistentProperty(ProjectProperties.RENDERER);
				will(returnValue(null));
				one(listener).propertyChange(with(propertyChangeEvent(projectProperties(), ProjectProperties.RENDERER.getLocalName(), null, "new")));
				one(project).setPersistentProperty(ProjectProperties.RENDERER, "new");
			}

		});
		projectProperties().addPropertyChangeListener(listener);
		projectProperties().setRenderer(project, "new");
	}

	@Test
	public void testPropertyChangeListenerNotCalledAfterItIsRemoved() throws CoreException {
		PropertyChangeListener listener = context.mock(PropertyChangeListener.class);
		context.checking(new Expectations() {
			{
				ignoring(project).getPersistentProperty(ProjectProperties.RENDERER);
				will(returnValue(null));
				one(project).setPersistentProperty(ProjectProperties.RENDERER, "new");
			}
		});
		projectProperties().addPropertyChangeListener(listener);
		projectProperties().removePropertyChangeListener(listener);
		projectProperties().setRenderer(project, "new");
	}

	private Matcher<PropertyChangeEvent> propertyChangeEvent(final Object source, final String propertyName, final Object oldValue, final Object newValue) {
		return new BaseMatcher<PropertyChangeEvent>() {

			public boolean matches(Object item) {
				PropertyChangeEvent event = (PropertyChangeEvent) item;
				return event.getSource().equals(source) && event.getPropertyName().equals(propertyName)
						&& oldValue == null ? true : oldValue.equals(event.getOldValue())
						&& newValue == null ? true : newValue.equals(event.getOldValue());
			}

			public void describeTo(Description description) {
				description.appendText("PropertyChangEvent");
			}

		};
	}
}
