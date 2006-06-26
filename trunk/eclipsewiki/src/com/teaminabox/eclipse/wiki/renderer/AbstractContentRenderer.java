package com.teaminabox.eclipse.wiki.renderer;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.WikiPlugin;
import com.teaminabox.eclipse.wiki.editors.WikiDocumentContext;
import com.teaminabox.eclipse.wiki.text.TextRegion;
import com.teaminabox.eclipse.wiki.text.TextRegionBuilder;
import com.teaminabox.eclipse.wiki.text.TextRegionMatcher;
import com.teaminabox.eclipse.wiki.text.WikiLinkTextRegion;

public abstract class AbstractContentRenderer implements ContentRenderer {

	public static final String	CLASS_MONO_SPACE	= "monospace";
	public static final String	CLASS_QUOTE			= "quote";
	public static final String	TABLE_DELIMITER		= "|";
	public static final String	HR					= "hr";
	public static final String	NEW_WIKIDOC_HREF	= "?";

	private WikiDocumentContext	context;
	private StringBuffer		buffer;
	private int					currentLine;
	private int					currentListDepth;
	private boolean				inTable;
	private String[]			document;
	private String				encoding;

	private LinkMaker			linkMaker;
	private TextRegionAppender	textRegionAppender;

	public abstract TextRegionMatcher[] getRendererMatchers();

	public abstract TextRegionMatcher[] getScannerMatchers();

	protected abstract void initialise();

	protected abstract char getListType(String line);

	protected abstract boolean isOrderedList(String line);

	protected abstract String getListText(String line);

	protected abstract int getListDepth(String line);

	protected abstract String processTags(String line);

	protected StringBuffer getBuffer() {
		return buffer;
	}

	public WikiDocumentContext getContext() {
		return context;
	}

	public final String render(WikiDocumentContext context, LinkMaker linkMaker) {
		initialise(context, linkMaker);
		try {
			appendHtmlHead();
			buffer.append("<h1>").append(WikiLinkTextRegion.deCamelCase(context.getWikiNameBeingEdited())).append("</h1>");
			appendNewLine();
			appendContents();
			appendHtmlFooter();
			return buffer.toString();
		} catch (Exception e) {
			WikiPlugin.getDefault().log(e.getLocalizedMessage(), e);
			return "<html><body><p>" + e.getLocalizedMessage() + "</p></body></html>";
		}
	}

	private void initialise(WikiDocumentContext context, LinkMaker linkMaker) {
		this.context = context;
		this.linkMaker = linkMaker;
		currentListDepth = 0;
		setInTable(false);
		encoding = context.getCharset().name();
		buffer = new StringBuffer();
		textRegionAppender = new TextRegionAppender(buffer, linkMaker);
		initialise();
	}

	private void appendHtmlHead() throws IOException {
		appendln("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>");
		appendln("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");
		appendln("<html>");
		appendln("  <head>");
		buffer.append("    <title>").append(WikiLinkTextRegion.deCamelCase(context.getWikiNameBeingEdited()));
		appendln("</title>");
		if (WikiPlugin.getDefault().getPluginPreferences().contains(WikiConstants.BROWSER_CSS_URL) && WikiPlugin.getDefault().getPluginPreferences().getString(WikiConstants.BROWSER_CSS_URL).trim().length() > 0) {
			buffer.append("      <link href=\"");
			buffer.append(WikiPlugin.getDefault().getPluginPreferences().getString(WikiConstants.BROWSER_CSS_URL));
			appendln("\" type=\"text/css\" rel=\"STYLESHEET\">");
		} else {
			appendStyle();
		}
		appendln("  </head>");
		appendln("  <body>");
	}

	private void appendContents() {
		document = context.getDocumentWithHeaderAndFooter();
		currentLine = 0;
		while (currentLine < document.length) {
			appendLine(document[currentLine]);
			currentLine++;
		}
	}

	protected void appendNewLine() {
		getBuffer().append(System.getProperty("line.separator"));
	}

	protected StringBuffer appendln(String text) {
		buffer.append(text);
		appendNewLine();
		return buffer;
	}

	protected String getNextLine() {
		if (hasLine(currentLine + 1)) {
			currentLine++;
			return document[currentLine];
		}
		throw new RuntimeException("Should not be called if there is no next line.");
	}

	protected String peekNextLine() {
		if (hasLine(currentLine + 1)) {
			return document[currentLine + 1];
		}
		return "";
	}

	protected boolean hasNextLine() {
		return hasLine(currentLine + 1);
	}

	private boolean hasLine(int lineNumber) {
		return lineNumber < document.length;
	}

	private void appendHtmlFooter() {
		appendln("  </body>").append("</html>");
	}

	protected void appendStyle() throws IOException {
		appendln("<style type=\"text/css\"><!--");
		IPath path = new Path("style").append(RendererFactory.getContentRendererName() + ".css");
		appendln(WikiPlugin.getDefault().loadTextContentsInPlugin(path));
		appendln("--></style>");
	}

	private void appendLine(String line) {
		if (isTableLine(line)) {
			processTable(line);
		} else if (isHeader(line)) {
			appendHeader(line);
		} else if (isList(line)) {
			appendListItem(line);
		} else if (process(line)) {
			return;
		} else {
			appendln("<p>");
			append(processTags(encode(line)));
			appendln("</p>");
		}
	}

	private final void appendListItem(String line) {
		boolean ordered = isOrderedList(line);
		char type = '1';
		if (ordered) {
			type = getListType(line);
		}
		String open = ordered ? "<ol type=\"" + type + "\">" : "<ul>";
		String close = ordered ? "</ol>" : "</ul>";
		int bullet = getListDepth(line);
		if (bullet > currentListDepth) {
			repeatAppend(open, bullet - currentListDepth);
			currentListDepth = bullet;
		} else if (bullet < currentListDepth) {
			repeatAppend(close, currentListDepth - bullet);
			currentListDepth = bullet;
		}
		getBuffer().append("<li>");
		String content = "";
		if (bullet < line.length() - 1) {
			content = getListText(line);
		}
		append(processTags(encode(content)));
		appendln("</li>");
		if (!isList(peekNextLine())) {
			repeatAppend(close, currentListDepth);
			currentListDepth = 0;
		}
	}

	protected void repeatAppend(String item, int n) {
		for (int i = 0; i < n; i++) {
			getBuffer().append(item);
		}
	}

	protected String encode(String line) {
		return StringEscapeUtils.escapeHtml(line);
	}

	protected void append(String line) {
		TextRegion[] regions = TextRegionBuilder.getTextRegions(line, context);
		for (int i = 0; i < regions.length; i++) {
			regions[i].accept(textRegionAppender);
		}
	}

	protected abstract boolean isList(String line);

	protected abstract boolean isHeader(String line);

	protected abstract void appendHeader(String line);

	/**
	 * Get the header from a line with header markup
	 * 
	 * @param line
	 *            guaranteed to be a valid header as defined by b {@link #isHeader(String) isHeader(String)}
	 */
	protected abstract String getHeaderText(String line);

	/**
	 * Gives implementors a chance to do processing on this line.
	 * 
	 * @return if true, the line will not be processed further
	 */
	protected abstract boolean process(String line);

	/**
	 * Replace all occurrences of markeup which occurs in pairs with an opening and closing tag in the given line. e.g.
	 * 
	 * <pre>
	 *       
	 *        
	 *         replacePair(&quot;my ''bold'' word&quot;, &quot;''&quot;, &quot;&lt;b&gt;&quot;, &quot;,&lt;/b&gt;&quot;) returns &quot;my &lt;b&gt;bold&lt;/b&gt; word&quot;
	 *         
	 *        
	 * </pre>
	 */
	protected String replacePair(String line, String search, String openingTag, String closingTag) {
		return replacePair(line, search, search, openingTag, closingTag);
	}

	protected String replacePair(String line, String openingSearch, String closingSearch, String openingTag, String closingTag) {
		int indexes = countTokens(line, openingSearch);
		for (int i = 0; i < indexes / 2; i++) {
			line = replaceFirst(line, openingSearch, openingTag);
			line = replaceFirst(line, closingSearch, closingTag);
		}
		return line;
	}

	private String replaceFirst(String line, String search, String replacement) {
		int index = line.indexOf(search);
		if (index < 0) {
			return line;
		}
		return new String(line.substring(0, index) + replacement + line.substring(index + search.length()));
	}

	private int countTokens(String line, String token) {
		int index = line.indexOf(token);
		int count = 0;
		while (index >= 0 && index < line.length()) {
			count++;
			index = line.indexOf(token, index + 1);
		}
		return count;
	}

	protected void appendHR() {
		appendln("<hr>");
	}

	protected boolean isTableLine(String line) {
		return line.startsWith(AbstractContentRenderer.TABLE_DELIMITER);
	}

	protected void processTable(String line) {
		if (!isInTable()) {
			setInTable(true);
			getBuffer().append(getTableTag());
		}
		getBuffer().append("<tr>");

		String[] cells = split(line, AbstractContentRenderer.TABLE_DELIMITER);
		for (int i = 0; i < cells.length; i++) {
			String cell = cells[i];
			String element = "td";
			if (cell.trim().startsWith("*")) {
				element = "th";
				cell = cell.replaceAll("\\*", "");
			}
			getBuffer().append("<").append(element).append(">");
			append(processTags(encode(cell)));
			getBuffer().append("</").append(element).append(">");
		}
		appendln("</tr>");
		if (!isTableLine(peekNextLine())) {
			appendln("</table>");
			setInTable(false);
		}
	}

	protected String[] split(String line, String delimiter) {
		if (line.indexOf(delimiter + delimiter) < 0) {
			return StringUtils.split(line, delimiter);
		}
		int index = line.indexOf(delimiter);
		if (index < 0) {
			return new String[] { line };
		}
		ArrayList bits = new ArrayList();
		int lastIndex = -1;
		while (index >= 0 && index < line.length()) {
			if (index > 0) {
				bits.add(new String(line.substring(lastIndex + 1, index)));
			}
			lastIndex = index;
			index = line.indexOf(delimiter, index + 1);
		}
		if (lastIndex < line.length() - 1) {
			bits.add(new String(line.substring(lastIndex + 1)));
		}
		return (String[]) bits.toArray(new String[bits.size()]);
	}

	/**
	 * @return the tag for table (can be overridden to add style)
	 */
	protected String getTableTag() {
		return "<table>";
	}

	public void forEachHeader(WikiDocumentContext context, StructureClosure closure) throws BadLocationException {
		String[] document = context.getDocument();
		for (int i = 0; i < document.length; i++) {
			String line = document[i];
			if (isHeader(line)) {
				String header = getHeaderText(line);
				closure.acceptHeader(header, i);
			}
		}
	}

	protected LinkMaker getLinkMaker() {
		return linkMaker;
	}

	protected void setInTable(boolean inTable) {
		this.inTable = inTable;
	}

	protected boolean isInTable() {
		return inTable;
	}

}