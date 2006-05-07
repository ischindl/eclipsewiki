package com.teaminabox.eclipse.wiki.renderer;

public final class WardsBrowserContentRenderer extends AbstractContentRenderer {

	public static final String	BULLET_MARKUP		= "*";

	public static final String	QUOTE_MARKUP_REGEX	= "^\t :\t.*";

	public static final String	HEADER_MARKUP_REGEX	= "^'''.+'''$";

	public static final String	HEADER_MARKUP		= "'''";

	public static final String	BOLD_MARKUP			= "__";

	public static final String	EMPHASIS_MARKUP		= "'''''";

	public static final String	ITALIC_MARKUP		= "''";

	public static final String	LIST_MARKUP_REGEX	= "^[\\*]+.*";

	public static final String	PLURAL				= "''''''s";

	protected void initialise() {

	}

	/**
	 * There aren't true headers in Ward's Wiki, just text in a <code>strong</code> element.
	 */
	protected void appendHeader(String line) {
		getBuffer().append("<p><strong>");
		getBuffer().append(encode(getHeaderText(line)));
		appendln("</strong></p>");
	}

	protected String getHeaderText(String line) {
		return line.replaceAll(WardsBrowserContentRenderer.HEADER_MARKUP, "");
	}

	protected int getListDepth(String line) {
		for (int i = 0; i < line.length(); i++) {
			if (line.charAt(i) != WardsBrowserContentRenderer.BULLET_MARKUP.charAt(0)) {
				return i;
			}
		}
		return line.length();
	}

	protected boolean isHeader(String line) {
		return !line.startsWith(WardsBrowserContentRenderer.EMPHASIS_MARKUP) && line.trim().matches(WardsBrowserContentRenderer.HEADER_MARKUP_REGEX);
	}

	protected boolean isList(String line) {
		return line.matches(WardsBrowserContentRenderer.LIST_MARKUP_REGEX);
	}

	protected boolean process(String line) {
		if (line.trim().matches("^-----*$")) {
			appendHR();
			return true;
		} else if (line.startsWith(" ")) {
			appendMonoSpacedLine(line);
			return true;
		} else if (line.matches(WardsBrowserContentRenderer.QUOTE_MARKUP_REGEX)) {
			appendQuote(line);
			return true;
		}
		return false;
	}

	private void appendQuote(String line) {
		getBuffer().append("<p class=\"").append(AbstractContentRenderer.CLASS_QUOTE).append("\">");
		append(processTags(encode(line.substring(4))));
		getBuffer().append("</p>");
	}

	private void appendMonoSpacedLine(String line) {
		getBuffer().append("<pre class=\"").append(AbstractContentRenderer.CLASS_MONO_SPACE).append("\">");
		appendNewLine();
		getBuffer().append(encode(line));
		while (hasNextLine() && peekNextLine().startsWith(" ")) {
			appendNewLine();
			getBuffer().append(encode(getNextLine()));
		}
		getBuffer().append("</pre>");
	}

	protected String processTags(String line) {
		line = line.replaceAll(WardsBrowserContentRenderer.PLURAL, "'s");
		line = replacePair(line, WardsBrowserContentRenderer.EMPHASIS_MARKUP, "<b><i>", "</i></b>");
		line = replacePair(line, WardsBrowserContentRenderer.HEADER_MARKUP, "<b>", "</b>");
		line = replacePair(line, WardsBrowserContentRenderer.BOLD_MARKUP, "<b>", "</b>");
		line = replacePair(line, WardsBrowserContentRenderer.ITALIC_MARKUP, "<i>", "</i>");
		return line;
	}

	protected boolean hasPreferredStyle() {
		return false;
	}

	protected String getListText(String line) {
		return line.substring(getListDepth(line)).trim();
	}

	/**
	 * Ward's renderer does not support ordered lists.
	 * 
	 * @return <code>FALSE</code>
	 */
	protected boolean isOrderedList(String line) {
		return false;
	}

	/**
	 * Should never be called.
	 * 
	 * @throws UnsupportedOperationException
	 */
	protected char getListType(String line) {
		throw new UnsupportedOperationException();
	}

}