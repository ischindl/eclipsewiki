package com.teaminabox.eclipse.wiki.editors.completion;

import org.eclipse.jdt.core.IProblemRequestor;
import org.eclipse.jdt.core.compiler.IProblem;

class NullProblemRequestor implements IProblemRequestor {
	public void acceptProblem(IProblem problem) {
	}

	public void beginReporting() {
	}

	public void endReporting() {
	}

	public boolean isActive() {
		return false;
	}
}