package com.teaminabox.eclipse.wiki.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.WikiPlugin;
import com.teaminabox.eclipse.wiki.editors.WikiDocumentContext;
import com.teaminabox.eclipse.wiki.renderer.RendererFactory;

import de.java2html.converter.JavaSource2HTMLConverter;
import de.java2html.javasource.JavaSource;
import de.java2html.javasource.JavaSourceParser;
import de.java2html.options.Java2HtmlConversionOptions;

public final class WikiExporter {

	private static final String	JAVA_EXTENSION	= "java";
	public static final String	HTML_EXTENSION	= ".html";

	public static final String	WORKSPACE		= "workspace";

	private File				exportDirectory;

	private ExportLinkMaker		exportLinkMaker;
	private TreeSet				index;

	public WikiExporter() {
		exportLinkMaker = new ExportLinkMaker();
		index = new TreeSet(String.CASE_INSENSITIVE_ORDER);
	}

	public void export(IContainer folder, String exportDirectoryName, IProgressMonitor monitor) throws IOException, CoreException {
		exportDirectory = new File(exportDirectoryName);
		IResource[] resources = folder.members(IResource.FILE);
		monitor.beginTask(WikiPlugin.getResourceString("Export.wikiPages"), resources.length + 1);
		for (int i = 0; i < resources.length; i++) {
			if (isWikiFile(resources[i])) {
				exportFile((IFile) resources[i]);
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
		Iterator iterator = index.iterator();
		while (iterator.hasNext()) {
			String name = (String) iterator.next();
			writer.print("    <br/>");
			writer.println("<a href=\"" + name + ".html\">" + name + "</a>");
		}
	}

	private void writeFooter(PrintWriter writer) {
		writer.println("  </body>");
		writer.println(" </html>");
	}

	private void writeHeader(PrintWriter writer) {
		writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		writer.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");
		writer.println("<html>");
		writer.println("  <head>");
		writer.print("    <title>Index</title>");
		writer.println("  </head>");
		writer.println("  <body>");
	}

	private void exportLinkedResources() throws IOException {
		if (!exportLinkMaker.hasLinkedDocuments()) {
			return;
		}
		File workspaceExport = new File(exportDirectory, WikiExporter.WORKSPACE);
		if (!workspaceExport.exists()) {
			workspaceExport.mkdir();
		}
		HashMap map = exportLinkMaker.getLinkedResources();
		Iterator iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			IResource resource = (IResource) iterator.next();
			String location = (String) map.get(resource);
			export(resource, location);
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
		JavaSource java = new JavaSourceParser().parse(new FileReader(source));
		JavaSource2HTMLConverter converter = new JavaSource2HTMLConverter(java);
		Java2HtmlConversionOptions options = Java2HtmlConversionOptions.getDefault();
		options.setShowLineNumbers(true);
		options.setShowFileName(true);
		options.setShowJava2HtmlLink(true);
		converter.setConversionOptions(options);
		FileWriter writer = new FileWriter(destination);
		converter.convert(writer);
		writer.flush();
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

	private boolean isWikiFile(IResource resource) {
		return resource instanceof IFile && WikiConstants.WIKI_FILE_EXTENSION.endsWith(resource.getFileExtension());
	}

	private void exportFile(IFile file) throws IOException {
		WikiDocumentContext context = new WikiDocumentContext(file);
		exportLinkMaker.setContext(context);
		String content = RendererFactory.createContentRenderer().render(context, exportLinkMaker);
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
