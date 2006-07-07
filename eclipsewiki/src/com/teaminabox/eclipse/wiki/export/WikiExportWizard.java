package com.teaminabox.eclipse.wiki.export;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.WikiPlugin;

public final class WikiExportWizard extends Wizard implements INewWizard {
	static final QualifiedName		DIRECTORY_QUALIFIED_NAME	= new QualifiedName(WikiConstants.PLUGIN_ID, "exportDirectory");

	private WikiExportWizardPage	page;
	private ISelection				selection;

	public WikiExportWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	public void addPages() {
		page = new WikiExportWizardPage(selection);
		addPage(page);
	}

	public boolean performFinish() {
		persistExportProperties();

		return runOperationInContainer(new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					new WikiExporter().export(page.getFolder(), page.getExportDirectoryPath(), monitor);
				} catch (Exception e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		});
	}

	private boolean runOperationInContainer(IRunnableWithProgress runnable) {
		try {
			getContainer().run(true, true, runnable);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			WikiPlugin.getDefault().log("", e);
			MessageDialog.openError(getShell(), "Error", e.getTargetException().getMessage());
			return false;
		}

		return true;
	}

	private void persistExportProperties() {
		IProject project = page.getFolder().getProject();
		try {
			project.setPersistentProperty(WikiExportWizard.DIRECTORY_QUALIFIED_NAME, new File(page.getExportDirectoryPath()).getAbsolutePath());
		} catch (CoreException cex) {
			noteException(cex);
		}
	}

	private void noteException(CoreException cex) {
		WikiPlugin.getDefault().log("Export Error", cex);
		throw new RuntimeException("An error occurred. Please see the log for details.");
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
}
