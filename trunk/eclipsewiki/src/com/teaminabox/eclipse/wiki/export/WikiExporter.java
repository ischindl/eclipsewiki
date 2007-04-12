package com.teaminabox.eclipse.wiki.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.TreeSet;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.teaminabox.eclipse.wiki.WikiPlugin;
import com.teaminabox.eclipse.wiki.editors.WikiDocumentContext;
import com.teaminabox.eclipse.wiki.util.JavaUtils;
import com.teaminabox.eclipse.wiki.util.Resources;

public final class WikiExporter {

	public static final String	HTML_EXTENSION	= ".html";
	public static final String	WORKSPACE		= "workspace";
	private static final String	JAVA_EXTENSION	= "java";

	private File				exportDirectory;

	private ExportLinkMaker		exportLinkMaker;
	private TreeSet<String>		index;

	public WikiExporter() {
		exportLinkMaker = new ExportLinkMaker();
		index = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
	}

	public void export(IContainer folder, String exportDirectoryName, IProgressMonitor monitor) throws IOException, CoreException {
		exportDirectory = new File(exportDirectoryName);
		IResource[] resources = folder.members(IResource.FILE);
		monitor.beginTask(WikiPlugin.getResourceString("Export.wikiPages"), resources.length + 1);
		for (IResource element : resources) {
			if (Resources.isWikiFile(element)) {
				exportFile((IFile) element);
			}
			monitor.worked(1);
		}
		monitor.subTask(WikiPlugin.getResourceString("Export.linkedResources"));
		exportLinkedResources();
		createIndex();
		monitor.worked(1);
	}

	/**
	 * TODO: This is a horrible hack for a quick solution.
	 */
	private void createIndex() throws IOException {
		File indexFile = createHtmlFile("index");

		PrintWriter writer = new PrintWriter(new FileWriter(indexFile));
		writeHeader(writer);
		writeContent(writer);
		writeFooter(writer);
		writer.flush();
		writer.close();
	}

	private void writeContent(PrintWriter writer) {
		for (String name : index) {
			writer.print("    <br/>");
			writer.println("<a href=\"" + name + ".html\">" + name + "</a>");
		}
	}

	private void writeFooter(PrintWriter writer) {
		writer.println("    <p><small>Generated by <a href=\"http://eclipsewiki.sourceforge.net\">EclipseWiki</a></small></p>");
		writer.println("  </body>");
		writer.println(" </html>");
	}

	private void writeHeader(PrintWriter writer) {
		writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		writer.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");
		writer.println("<html>");
		writer.println("  <head>");
		writer.println("    <title>Index</title>");
		writer.println("  </head>");
		writer.println("  <body>");
	}

	private void exportLinkedResources() throws IOException {
		if (!exportLinkMaker.hasLinkedDocuments()) {
			return;
		}
		createWorkspaceFolder();
		Map<IResource, String> map = exportLinkMaker.getLinkedResources();
		for (Map.Entry<IResource, String> entry : map.entrySet()) {
			export(entry.getKey(), entry.getValue());
		}
	}

	private void createWorkspaceFolder() {
		File workspaceExport = new File(exportDirectory, WikiExporter.WORKSPACE);
		if (!workspaceExport.exists()) {
			workspaceExport.mkdir();
		}
	}

	private void export(IResource resource, String location) throws IOException {
		File destination = new File(exportDirectory, location);

		if (destination.isDirectory()) {
			return;
		}
		if (!destination.exists()) {
			destination.getParentFile().mkdirs();
		}
		File source = new File(resource.getLocation().toString());
		if (isJavaResource(resource)) {
			javaToHtml(source, new File(destination.getParentFile(), destination.getName()));
		} else {
			copy(source, destination);
		}
	}

	private boolean isJavaResource(IResource resource) {
		return WikiExporter.JAVA_EXTENSION.equals(resource.getFileExtension());
	}

	private void javaToHtml(File source, File destination) throws IOException {
		Reader reader = new FileReader(source);
		Writer writer = new FileWriter(destination);
		JavaUtils.writeJava(reader, writer);
		writer.close();
	}

	private void copy(File source, File dest) throws IOException {
		FileChannel in = null;
		FileChannel out = null;
		try {
			in = new FileInputStream(source).getChannel();
			out = new FileOutputStream(dest).getChannel();
			long size = in.size();
			MappedByteBuffer buf = in.map(FileChannel.MapMode.READ_ONLY, 0, size);
			out.write(buf);
		} finally {
			close(in);
			close(out);
		}
	}

	private void close(FileChannel channel) throws IOException {
		if (channel != null) {
			channel.close();
		}
	}

	private void exportFile(IFile file) throws IOException, CoreException {
		WikiDocumentContext context = new WikiDocumentContext(file);
		exportLinkMaker.setContext(context);
		String content = context.getContentRenderer().render(context, exportLinkMaker, false);
		FileWriter writer = new FileWriter(createHtmlFile(context.getWikiNameBeingEdited()));
		writer.write(content);
		writer.flush();
		writer.close();
		index.add(context.getWikiNameBeingEdited());
	}

	private File createHtmlFile(String name) {
		return new File(exportDirectory, name + WikiExporter.HTML_EXTENSION);
	}
}
