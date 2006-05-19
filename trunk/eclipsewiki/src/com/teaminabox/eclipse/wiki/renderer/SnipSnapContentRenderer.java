/*
 * Thanks to Bent Andre Solheim for the improved header rendering.
 */
package com.teaminabox.eclipse.wiki.renderer;

import com.teaminabox.eclipse.wiki.text.BasicWikiNameMatcher;
import com.teaminabox.eclipse.wiki.text.EclipseResourceMatcher;
import com.teaminabox.eclipse.wiki.text.ForcedLinkMatcher;
import com.teaminabox.eclipse.wiki.text.IgnoredTextRegionMatcher;
import com.teaminabox.eclipse.wiki.text.JavaTypeMatcher;
import com.teaminabox.eclipse.wiki.text.LetterOrDigitMatcher;
import com.teaminabox.eclipse.wiki.text.NonLetterOrDigitMatcher;
import com.teaminabox.eclipse.wiki.text.PluginResourceMatcher;
import com.teaminabox.eclipse.wiki.text.TextRegionMatcher;
import com.teaminabox.eclipse.wiki.text.UrlMatcher;
import com.teaminabox.eclipse.wiki.text.WikiSpaceMatcher;

public final class SnipSnapContentRenderer extends AbstractContentRenderer {

	public static TextRegionMatcher[]	RENDERER_MATCHERS			= new TextRegionMatcher[] { new IgnoredTextRegionMatcher(), new UrlMatcher(), new EclipseResourceMatcher(), new PluginResourceMatcher(), new WikiSpaceMatcher(), new JavaTypeMatcher(), new ForcedLinkMatcher(1), new BasicWikiNameMatcher(), new NonLetterOrDigitMatcher(), new LetterOrDigitMatcher(), };

	public static TextRegionMatcher[]	SCANNER_MATCHERS			= new TextRegionMatcher[] { new IgnoredTextRegionMatcher(), new UrlMatcher(), new EclipseResourceMatcher(), new PluginResourceMatcher(), new WikiSpaceMatcher(), new JavaTypeMatcher(), new ForcedLinkMatcher(1), new BasicWikiNameMatcher(), };

	public static final String			UNORDERED_LIST_MARKUP		= "*";
	public static final String			ALT_UNORDERED_LIST_MARKUP	= "-";

	public static final String			ORDERED_LIST_END_MARKER		= ".";
	public static final String			ORDERED_LIST_REGEX			= "^[1|a|A|i|I]\\..*";
	public static final String			TABLE_MACRO					= "{table}";
	private boolean						tableHeader;

	public TextRegionMatcher[] getRendererMatchers() {
		return SnipSnapContentRenderer.RENDERER_MATCHERS;
	}

	public TextRegionMatcher[] getScannerMatchers() {
		return SnipSnapContentRenderer.SCANNER_MATCHERS;
	}

	protected void initialise() {

	}

	protected void appendHeader(String line) {
		String header = encode(getHeaderText(line));
		String headerStartTag = "<h1 " + getHeaderCss(getHeaderSize(line)) + ">";
		getBuffer().append(headerStartTag);
		getBuffer().append(header);
		getBuffer().append("</h1>");
	}

	private int getHeaderSize(String line) {
		int size = 0;
		int index = 0;
		while (index < line.length() && line.charAt(index) != ' ') {
			if (line.charAt(index) == '1') {
				size++;
			}
			index++;
		}
		return size;
	}

	private String getHeaderCss(int size) {
		int max = Math.min(3, size);
		StringBuffer css = new StringBuffer();
		css.append("class=\"").append("heading");
		for (int i = 0; i < max; i++) {
			css.append("-1");
		}
		css.append("\"");
		return css.toString();
	}

	protected String getHeaderText(String line) {
		return line.substring(line.indexOf(' ')).trim();
	}

	protected boolean isHeader(String line) {
		return line.startsWith("1 ") || line.startsWith("1.1 ");
	}

	protected boolean isTableLine(String line) {
		// toggle isInTable on {table} mark up
		if (SnipSnapContentRenderer.TABLE_MACRO.equals(line)) {
			setInTable(!isInTable());
		}
		return isInTable();
	}

	protected void processTable(String line) {
		if (SnipSnapContentRenderer.TABLE_MACRO.equals(line)) {
			tableHeader = true;
			appendln(getTableTag());
			return;
		}
		getBuffer().append("<tr>");
		String[] cells = split(line, AbstractContentRenderer.TABLE_DELIMITER);
		for (int i = 0; i < cells.length; i++) {
			String element = tableHeader ? "th" : "td";
			getBuffer().append("<").append(element).append(">");
			append(processTags(encode(cells[i])));
			getBuffer().append("</").append(element).append(">");
		}
		appendln("</tr>");
		if (SnipSnapContentRenderer.TABLE_MACRO.equals(peekNextLine())) {
			appendln("</table>");
		}
		tableHeader = false;
	}

	protected boolean process(String line) {
		if (line.trim().matches("^----*$")) {
			appendHR();
			return true;
		}
		return false;
	}

	protected String processTags(String line) {
		line = line.replaceAll("\\\\", "<br>");
		line = extractMacroMarkup(line);
		line = replacePair(line, "__", "<b>", "</b>");
		line = replacePair(line, "~~", "<i>", "</i>");
		line = replacePair(line, "--", "<strike>", "</strike>");
		return line;
	}

	private String extractMacroMarkup(String line) {
		return line.replaceAll("\\{.*?\\}", "");
	}

	protected String getTableTag() {
		return "<table class=\"wiki-table\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">";
	}

	protected String getListText(String line) {
		if (isOrderedList(line)) {
			return line.substring(2).trim();
		}
		return line.substring(getListDepth(line)).trim();
	}

	protected char getListType(String line) {
		return line.charAt(0);
	}

	protected boolean isList(String line) {
		return isOrderedList(line) || isUnorderedList(line);
	}

	private boolean isUnorderedList(String line) {
		return !line.trim().equals("----") && (line.startsWith(SnipSnapContentRenderer.UNORDERED_LIST_MARKUP) || line.startsWith(SnipSnapContentRenderer.ALT_UNORDERED_LIST_MARKUP));
	}

	protected boolean isOrderedList(String line) {
		return line.matches(SnipSnapContentRenderer.ORDERED_LIST_REGEX);
	}

	protected int getListDepth(String line) {
		if (isOrderedList(line)) {
			return 1;
		}
		char bullet = getBullet(line);
		for (int i = 0; i < line.length(); i++) {
			if (line.charAt(i) != bullet) {
				return i;
			}
		}
		return line.length();
	}

	private char getBullet(String line) {
		char bullet = line.startsWith(SnipSnapContentRenderer.UNORDERED_LIST_MARKUP) ? SnipSnapContentRenderer.UNORDERED_LIST_MARKUP.charAt(0) : SnipSnapContentRenderer.ALT_UNORDERED_LIST_MARKUP.charAt(0);
		return bullet;
	}
}