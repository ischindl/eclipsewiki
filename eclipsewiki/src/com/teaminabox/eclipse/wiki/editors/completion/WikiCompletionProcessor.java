package com.teaminabox.eclipse.wiki.editors.completion;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationPresenter;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.WikiPlugin;
import com.teaminabox.eclipse.wiki.editors.WikiDocumentContext;
import com.teaminabox.eclipse.wiki.editors.WikiEditor;
import com.teaminabox.eclipse.wiki.text.BasicTextRegion;
import com.teaminabox.eclipse.wiki.text.EclipseResourceTextRegion;
import com.teaminabox.eclipse.wiki.text.GenericTextRegionVisitor;
import com.teaminabox.eclipse.wiki.text.PluginProjectSupport;
import com.teaminabox.eclipse.wiki.text.PluginResourceTextRegion;
import com.teaminabox.eclipse.wiki.text.TextRegion;
import com.teaminabox.eclipse.wiki.text.TextRegionBuilder;
import com.teaminabox.eclipse.wiki.text.UndefinedTextRegion;

public final class WikiCompletionProcessor implements IContentAssistProcessor {

	private static final String					WIKI_FOLDER					= "wiki";

	private static final char[]					AUTO_ACTIVATION_CHARACTERS	= new char[] { '.', '/' };

	private static final ICompletionProposal[]	EMPTY_COMPLETIONS			= new ICompletionProposal[0];

	protected static class Validator implements IContextInformationValidator, IContextInformationPresenter {

		protected int	fInstallOffset;

		public boolean isContextInformationValid(int offset) {
			return false;
		}

		public void install(IContextInformation info, ITextViewer viewer, int offset) {
			fInstallOffset = offset;
		}

		public boolean updatePresentation(int documentPosition, TextPresentation presentation) {
			return false;
		}
	}

	private IContextInformationValidator	validator	= new Validator();

	private WikiEditor						wikiEditor;

	private JavaCompletionProcessor			javaCompletionProcessor;

	public WikiCompletionProcessor(WikiEditor wikiEditor) {
		this.wikiEditor = wikiEditor;
		javaCompletionProcessor = new JavaCompletionProcessor();
	}

	public ICompletionProposal[] computeCompletionProposals(final ITextViewer viewer, int documentOffset) {
		try {
			ArrayList proposals = new ArrayList();
			IProject project = wikiEditor.getContext().getWorkingLocation().getProject();
			TextRegion textRegion = TextRegionBuilder.getTextRegionAtCursor(wikiEditor, viewer.getDocument(), documentOffset);
			boolean tryJava = true;
			if (findNonJavaCompletions(viewer, documentOffset)) {
				ArrayList wikiProposals = new ArrayList();
				if (textRegion.getText().startsWith(WikiConstants.ECLIPSE_PREFIX)) {
					wikiProposals = computeEclipseResourceCompletionProposals(viewer, documentOffset);
				} else if (textRegion.getText().startsWith(WikiConstants.PLUGIN_PREFIX)) {
					wikiProposals = computePluginResourceCompletionProposals(viewer, documentOffset);
				}

				if (wikiProposals.size() == 0) {
					wikiProposals = getProposals(documentOffset, textRegion);
				} else {
					tryJava = false;
				}
				proposals.addAll(wikiProposals);
			}

			if (tryJava && project.hasNature(JavaCore.NATURE_ID)) {
				IJavaProject javaProject = JavaCore.create(project);
				proposals.addAll(javaCompletionProcessor.getProposals(javaProject, viewer, documentOffset));
			}
			return (ICompletionProposal[]) proposals.toArray(new ICompletionProposal[proposals.size()]);
		} catch (Exception e) {
			WikiPlugin.getDefault().logAndReport("Completion Processor", e.getLocalizedMessage(), e);
			return WikiCompletionProcessor.EMPTY_COMPLETIONS;
		}
	}

	private boolean findNonJavaCompletions(final ITextViewer viewer, int documentOffset) throws BadLocationException {
		return viewer.getDocument().getLength() > 0 && getCharacterAtDocumentOffset(viewer, documentOffset) != '.';
	}

	/**
	 * Get the character position of the document offset (which is the cursor position). The documentOffset when the
	 * cursor is after the last character then it is == viewer.getDocument().getLength(). In which case documentOffset -
	 * 1 is returned.
	 * 
	 * @throws BadLocationException
	 */
	private int getCharacterAtDocumentOffset(final ITextViewer viewer, int documentOffset) throws BadLocationException {
		if (documentOffset == viewer.getDocument().getLength() && viewer.getDocument().getLength() > 0) {
			return viewer.getDocument().getChar(documentOffset - 1);
		}
		return viewer.getDocument().getChar(documentOffset);
	}

	private ArrayList computeEclipseResourceCompletionProposals(final ITextViewer viewer, final int documentOffset) throws BadLocationException {
		IRegion region = viewer.getDocument().getLineInformationOfOffset(documentOffset);
		String line = viewer.getDocument().get(region.getOffset(), region.getLength());
		int eclipseLinkIndex = line.indexOf(WikiConstants.ECLIPSE_PREFIX);
		int cursorPositionInLine = documentOffset - region.getOffset();
		if (eclipseLinkIndex < 0 || eclipseLinkIndex > cursorPositionInLine) {
			return new ArrayList();
		}

		// Get the link around the cursor position
		int nextEclipseLink = line.indexOf(WikiConstants.ECLIPSE_PREFIX, eclipseLinkIndex + 1);
		while (nextEclipseLink > 0 && nextEclipseLink < cursorPositionInLine) {
			eclipseLinkIndex = nextEclipseLink;
			nextEclipseLink = line.indexOf(WikiConstants.ECLIPSE_PREFIX, eclipseLinkIndex + 1);
		}
		String linkText = new String(line.substring(eclipseLinkIndex, cursorPositionInLine));

		EclipseResourceTextRegion eclipseResourceTextRegion = new EclipseResourceTextRegion(linkText);
		eclipseResourceTextRegion.setCursorPosition(cursorPositionInLine - eclipseLinkIndex);
		return getProposals(documentOffset, eclipseResourceTextRegion);
	}

	private ArrayList computePluginResourceCompletionProposals(final ITextViewer viewer, final int documentOffset) throws BadLocationException {
		IRegion region = viewer.getDocument().getLineInformationOfOffset(documentOffset);
		String line = viewer.getDocument().get(region.getOffset(), region.getLength());
		int pluginLinkIndex = line.indexOf(WikiConstants.PLUGIN_PREFIX);
		int cursorPositionInLine = documentOffset - region.getOffset();
		if (pluginLinkIndex < 0 || pluginLinkIndex > cursorPositionInLine) {
			return new ArrayList();
		}

		// Get the link around the cursor position
		int nextPluginLink = line.indexOf(WikiConstants.PLUGIN_PREFIX, pluginLinkIndex + 1);
		while (nextPluginLink > 0 && nextPluginLink < cursorPositionInLine) {
			pluginLinkIndex = nextPluginLink;
			nextPluginLink = line.indexOf(WikiConstants.PLUGIN_PREFIX, pluginLinkIndex + 1);
		}
		String linkText = new String(line.substring(pluginLinkIndex, cursorPositionInLine));

		PluginResourceTextRegion pluginResourceTextRegion = new PluginResourceTextRegion(linkText);
		pluginResourceTextRegion.setCursorPosition(cursorPositionInLine - pluginLinkIndex);
		return getProposals(documentOffset, pluginResourceTextRegion);
	}

	private ArrayList getProposals(final int documentOffset, final TextRegion textRegion) {
		ArrayList list = (ArrayList) textRegion.accept(new GenericTextRegionVisitor(new ArrayList()) {
			public Object visit(UndefinedTextRegion undefinedTextRegion) {
				return getPotentialWikiNameCompletion(textRegion.getTextToCursor(), documentOffset);
			}

			public Object visit(BasicTextRegion basicTextRegion) {
				return getPotentialWikiNameCompletion(textRegion.getTextToCursor(), documentOffset);
			}

			public Object visit(EclipseResourceTextRegion eclipseResourceTextRegion) {
				int colon = textRegion.getTextToCursor().indexOf(WikiConstants.WIKISPACE_DELIMITER) + 1;
				String location = new String(textRegion.getTextToCursor().substring(colon));
				return getResourceCompletions(textRegion.getTextToCursor(), location, documentOffset);
			}

			public Object visit(PluginResourceTextRegion pluginResourceTextRegion) {
				int colon = textRegion.getTextToCursor().indexOf(WikiConstants.WIKISPACE_DELIMITER) + 1;
				String location = new String(textRegion.getTextToCursor().substring(colon));
				IPath path = PluginResourceTextRegion.getPluginPath(location);
				int slashPos = location.indexOf('/');
				if (slashPos < 0 || slashPos == location.lastIndexOf('/') && slashPos - 1 == location.length()) {
					return getPluginCompletions(textRegion.getTextToCursor(), location, documentOffset);
				}
				return getPluginCompletions(textRegion.getTextToCursor(), location, documentOffset, path);
			}
		});

		addWikiSpaceCompletions(textRegion.getTextToCursor(), list, documentOffset);
		return list;
	}

	private Object getPluginCompletions(String text, String location, int documentOffset, IPath path) {
		try {
			int lengthToBeReplaced = 0;
			int replacementOffset = documentOffset;
			int colon = text.indexOf(WikiConstants.WIKISPACE_DELIMITER);
			int lastSlash = text.lastIndexOf(WikiConstants.PATH_SEPARATOR);
			int lastSegment = colon > lastSlash ? colon : lastSlash;
			if (lastSegment != -1) {
				lengthToBeReplaced = text.length() - lastSegment - 1;
				replacementOffset = documentOffset - lengthToBeReplaced;
			}
			String rest = "";
			if (path == null) {
				int slashPos = location.lastIndexOf('/');
				String base = new String(location.substring(0, slashPos));
				path = PluginResourceTextRegion.getPluginPath(base);
				rest = new String(location.substring(slashPos + 1));
			}
			String[] children = getChildren(path.toString() + rest);
			return buildResourceProposals(children, "", replacementOffset, lengthToBeReplaced);
		} catch (Exception e) {
			WikiPlugin.getDefault().logAndReport("Completion Error", e.getLocalizedMessage(), e);
			return new ArrayList(1);
		}
	}

	private ArrayList getPluginCompletions(String text, String location, int documentOffset) {
		try {
			int lengthToBeReplaced = 0;
			int replacementOffset = documentOffset;
			int colon = text.indexOf(WikiConstants.WIKISPACE_DELIMITER);
			int lastSlash = text.lastIndexOf(WikiConstants.PATH_SEPARATOR);
			int lastSegment = colon > lastSlash ? colon : lastSlash;
			if (lastSegment != -1) {
				lengthToBeReplaced = text.length() - lastSegment - 1;
				replacementOffset = documentOffset - lengthToBeReplaced;
			}

			String[] children = collectPlugIDs(location);
			return buildResourceProposals(children, "", replacementOffset, lengthToBeReplaced);
		} catch (Exception e) {
			WikiPlugin.getDefault().logAndReport("Completion Error", e.getLocalizedMessage(), e);
			return new ArrayList(1);
		}
	}

	/*
	 * collect plugin-ID's of those plugin's, that have a folder wiki. the plugin location is taken either from the
	 * workspace or (if the plugin does not exists in the workspace) from the plugins folder of Eclipse.
	 */
	private String[] collectPlugIDs(String path) {
		if (path == null) {
			path = "";
		}
		Set plugIds = gatherPluginIds(path);
		SortedMap selectedIDs = new TreeMap();

		for (Iterator ids = plugIds.iterator(); ids.hasNext();) {
			String currPluginID = (String) ids.next();
			addWikiFromPlugin(path, currPluginID, selectedIDs);
		}
		return (String[]) selectedIDs.values().toArray(new String[selectedIDs.size()]);
	}

	private void addWikiFromPlugin(String path, String currPluginID, SortedMap selectedIDs) {
		if (path.length() == 0 || currPluginID.startsWith(path)) {
			IPath plugDirPath = null;
			IProject proj = PluginProjectSupport.locateProjectInWorkspace(currPluginID);
			if (proj != null) {
				plugDirPath = proj.getRawLocation();
			} else {
				plugDirPath = PluginResourceTextRegion.getPluginPath(currPluginID);
			}
			if (plugDirPath != null) {
				File plugDir = plugDirPath.toFile();
				if (plugDir != null && plugDir.exists()) {
					if (new File(plugDir, WIKI_FOLDER).exists()) {
						selectedIDs.put(currPluginID, currPluginID);
					}
				}
			}
		}
	}

	private Set gatherPluginIds(String path) {
		Set plugIds = new HashSet();
		if (path.length() == 0) {
			getPluginsFromWorkspace(plugIds);
		}
		IExtensionRegistry extensionRegistry = org.eclipse.core.runtime.Platform.getExtensionRegistry();
		for (int i = 0; i < extensionRegistry.getNamespaces().length; i++) {
			String id = extensionRegistry.getNamespaces()[i];
			plugIds.add(id);
		}
		return plugIds;
	}

	private void getPluginsFromWorkspace(Set plugIds) {
		String[] projects = getProjectList("");
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		for (int i = 0; i < projects.length; i++) {
			String projName = projects[i];
			IProject proj = root.getProject(projName);
			if (proj.getFile("plugin.xml").exists() || proj.getFile("fragment.xml").exists()) {
				String id = PluginProjectSupport.extractPlugID(proj);
				if (id != null) {
					plugIds.add(id);
				}
			}
		}
	}

	private void addWikiSpaceCompletions(String text, ArrayList list, int documentOffset) {
		addLocalWikiSpaceCompletions(text, list, documentOffset);
		String word = text.toLowerCase().trim();
		Iterator wikispace = wikiEditor.getContext().getWikiSpace().keySet().iterator();
		while (wikispace.hasNext()) {
			String name = (String) wikispace.next();
			if (name.toLowerCase().startsWith(word)) {
				String completion = name + WikiConstants.WIKISPACE_DELIMITER;
				ICompletionProposal proposal = new CompletionProposal(completion, documentOffset - word.length(), word.length(), completion.length(), WikiPlugin.getDefault().getImageRegistry().get(WikiConstants.WIKI_SPACE_ICON), null, null, null);
				list.add(proposal);
			}
		}
	}

	/**
	 * Get WikiSpace completions for local resources
	 * 
	 * @param word
	 *            text to the cursor which is at the documentOffset
	 * @param list
	 *            the list to add completions too
	 * @param documentOffset
	 *            position of cursor and where <code>word</code> ends
	 */
	private void addLocalWikiSpaceCompletions(String word, ArrayList list, int documentOffset) {
		if (word.indexOf(WikiConstants.WIKISPACE_DELIMITER) < 0) {
			return;
		}
		String wikiSpace = new String(word.substring(0, word.indexOf(WikiConstants.WIKISPACE_DELIMITER)));
		WikiDocumentContext context = wikiEditor.getContext();
		if (isEclipseWikispaceLink(wikiSpace, context)) {
			String locationPrefix = new String(context.getWikiSpaceLink(wikiSpace).substring(WikiConstants.ECLIPSE_PREFIX.length()));
			// + 1 below to get the WIKISPACE_DELIMITER (:)
			String location = new String(locationPrefix + word.substring(wikiSpace.length() + 1));
			list.addAll(getResourceCompletions(word, location, documentOffset));
		} else if (isPluginWikispaceLink(wikiSpace, context)) {
			String locationPrefix = new String(context.getWikiSpaceLink(wikiSpace).substring(WikiConstants.PLUGIN_PREFIX.length()));
			// + 1 below to get the WIKISPACE_DELIMITER (:)
			String location = new String(locationPrefix + word.substring(wikiSpace.length() + 1));
			list.addAll(getResourceCompletions(word, location, documentOffset));
		}
	}

	private boolean isPluginWikispaceLink(String wikiSpace, WikiDocumentContext context) {
		return context.getWikiSpace().containsKey(wikiSpace) && context.getWikiSpaceLink(wikiSpace).startsWith(WikiConstants.PLUGIN_PREFIX);
	}

	private boolean isEclipseWikispaceLink(String wikiSpace, WikiDocumentContext context) {
		return context.getWikiSpace().containsKey(wikiSpace) && context.getWikiSpaceLink(wikiSpace).startsWith(WikiConstants.ECLIPSE_PREFIX);
	}

	private ArrayList getResourceCompletions(String text, String location, int documentOffset) {
		try {
			int lengthToBeReplaced = 0;
			int replacementOffset = documentOffset;
			int colon = text.indexOf(WikiConstants.WIKISPACE_DELIMITER);
			int lastSlash = text.lastIndexOf(WikiConstants.PATH_SEPARATOR);
			int lastSegment = colon > lastSlash ? colon : lastSlash;
			if (lastSegment != -1) {
				lengthToBeReplaced = text.length() - lastSegment - 1;
				replacementOffset = documentOffset - lengthToBeReplaced;
			}

			String[] children = getChildren(location);
			return buildResourceProposals(children, "", replacementOffset, lengthToBeReplaced);
		} catch (Exception e) {
			WikiPlugin.getDefault().logAndReport("Completion Error", e.getLocalizedMessage(), e);
			return new ArrayList();
		}
	}

	/**
	 * @param replacements
	 *            the list of completions
	 * @param prefix
	 *            text to prefix each replacement with
	 * @param replacementOffset
	 *            see CompletionProposal#CompletionProposal(String, int, int, int)
	 * @param replacementLength
	 *            see CompletionProposal#CompletionProposal(String, int, int, int)
	 * @return ICompletionProposal[]
	 * @see CompletionProposal#CompletionProposal(String, int, int, int)
	 */
	private ArrayList buildResourceProposals(String[] replacements, String prefix, int replacementOffset, int replacementLength) {
		ArrayList list = new ArrayList();
		for (int i = 0; i < replacements.length; i++) {
			String child = prefix + replacements[i];
			list.add(new CompletionProposal(child, replacementOffset, replacementLength, child.length(), WikiPlugin.getDefault().getImageRegistry().get(WikiConstants.WIKI_RESOURCE_ICON), null, null, null));
		}
		return list;
	}

	private String[] getChildren(String path) throws CoreException {
		if (path.length() == 0 || path.equals(WikiConstants.PATH_SEPARATOR) || path.indexOf(WikiConstants.PATH_SEPARATOR, 1) == -1) {
			return getProjectList(path);
		}
		// path must be at least one segment at this point (project name)
		IPath relPath = new Path(path);
		if (relPath.hasTrailingSeparator()) {
			return getChildren(relPath, "");
		}

		String lastBit = relPath.lastSegment();
		relPath = relPath.removeLastSegments(1);
		return getChildren(relPath, lastBit);
	}

	private String[] getChildren(IPath parent, String resourcePrefix) throws CoreException {
		IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(parent);
		if (resource == null) {
			File xfile = parent.toFile();
			if (xfile.exists()) {
				return getChildren(resourcePrefix, xfile);
			}
		} else if (resource.exists() && (resource.getType() == IResource.FOLDER || resource.getType() == IResource.PROJECT)) {
			return getChildren(resourcePrefix, resource);
		}
		return new String[0];
	}

	private String[] getChildren(String resourcePrefix, File parent) {
		SortedMap sort = new TreeMap();
		File[] files = parent.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.getName().startsWith(resourcePrefix)) {
				sort.put(file.getName(), file.getName());
			}
		}
		Collection values = sort.values();
		return (String[]) values.toArray(new String[values.size()]);
	}

	private String[] getChildren(String resourcePrefix, IResource resource) throws CoreException {
		IContainer container = (IContainer) resource;
		IResource[] children = container.members();
		ArrayList childNames = new ArrayList();
		for (int i = 0; i < children.length; i++) {
			if (children[i].getName().startsWith(resourcePrefix)) {
				childNames.add(children[i].getName());
			}
		}
		return (String[]) childNames.toArray(new String[childNames.size()]);
	}

	private String[] getProjectList(String text) {
		String prefix = text;
		if (prefix != null && prefix.startsWith(WikiConstants.PATH_SEPARATOR)) {
			prefix = new String(prefix.substring(1));
		}
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		ArrayList names = new ArrayList();
		for (int i = 0; i < projects.length; i++) {
			IProject proj = projects[i];
			if (prefix == null || prefix.length() == 0 || proj.getName().startsWith(prefix)) {
				names.add(proj.getName());
			}
		}
		return (String[]) names.toArray(new String[names.size()]);
	}

	private ArrayList getPotentialWikiNameCompletion(String text, int documentOffset) {
		String word = text.trim();
		try {
			if (word.length() > 0 && !Character.isUpperCase(word.charAt(0))) {
				return new ArrayList();
			}
			IResource[] resources = wikiEditor.getContext().getWorkingLocation().members(IResource.FILE);
			ArrayList list = new ArrayList();
			for (int i = 0; i < resources.length; i++) {
				String name = resources[i].getName();
				if (isWikiFile(name) && name.startsWith(word)) {
					String wikiName = getWikiWord(name);
					ICompletionProposal proposal = new CompletionProposal(wikiName, documentOffset - word.length(), word.length(), wikiName.length(), WikiPlugin.getDefault().getImageRegistry().get(WikiConstants.WIKI_ICON), null, null, null);
					list.add(proposal);
				}
			}
			return list;
		} catch (CoreException e) {
			WikiPlugin.getDefault().logAndReport("Completion Error", e.getLocalizedMessage(), e);
			return new ArrayList();
		}
	}

	private String getWikiWord(String fileName) {
		return new String(fileName.substring(0, fileName.indexOf(WikiConstants.WIKI_FILE_EXTENSION)));
	}

	private boolean isWikiFile(String name) {
		return name.endsWith(WikiConstants.WIKI_FILE_EXTENSION);
	}

	public IContextInformation[] computeContextInformation(ITextViewer viewer, int documentOffset) {
		return new IContextInformation[0];
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getCompletionProposalAutoActivationCharacters()
	 */
	public char[] getCompletionProposalAutoActivationCharacters() {
		return WikiCompletionProcessor.AUTO_ACTIVATION_CHARACTERS;
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationAutoActivationCharacters()
	 */
	public char[] getContextInformationAutoActivationCharacters() {
		return new char[0];
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getErrorMessage()
	 */
	public String getErrorMessage() {
		return null;
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationValidator()
	 */
	public IContextInformationValidator getContextInformationValidator() {
		return validator;
	}

}