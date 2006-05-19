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

public final class TwikiBrowserContentRenderer extends AbstractContentRenderer {

	private static final TextRegionMatcher[]	RENDERER_MATCHERS			= new TextRegionMatcher[] { new IgnoredTextRegionMatcher(), new JavaTypeMatcher(), new ForcedLinkMatcher(2), new BasicWikiNameMatcher(), new NonLetterOrDigitMatcher(), new LetterOrDigitMatcher(), new UrlMatcher(), new EclipseResourceMatcher(), new PluginResourceMatcher(), new WikiSpaceMatcher() };

	private static final TextRegionMatcher[]	SCANNER_MATCHERS			= new TextRegionMatcher[] { new IgnoredTextRegionMatcher(), new JavaTypeMatcher(), new ForcedLinkMatcher(2), new BasicWikiNameMatcher(), new UrlMatcher(), new EclipseResourceMatcher(), new PluginResourceMatcher(), new WikiSpaceMatcher() };

	private static final char				ORDERED_LIST_END_MARKER		= '.';

	private static final String				UNORDERED_LIST_MARKUP		= "*";
	private static final String				UNORDERED_LIST_MARKUP_REGEX	= "^\\s+\\*\\s.*";
	private static final String				ORDERED_LIST_MARKUP_REGEX	= "^\\s+[1|a|A|i|I]\\.\\s.*";
	private static final String				HEADER_MARKUP_REGEX			= "^---+(\\++|\\#+)\\s*(.+)\\s*$";

	public TextRegionMatcher[] getRendererMatchers() {
		return TwikiBrowserContentRenderer.RENDERER_MATCHERS;
	}

	public TextRegionMatcher[] getScannerMatchers() {
		return TwikiBrowserContentRenderer.SCANNER_MATCHERS;
	}

	protected void initialise() {
	}

	protected void appendHeader(String line) {
		int headerSize = getHeaderSize(line);
		getBuffer().append("<h").append(headerSize).append(">");
		getBuffer().append(getHeaderText(line));
		getBuffer().append("</h").append(headerSize).append(">");
	}

	protected String getHeaderText(String line) {
		return line.substring(getHeaderStart(line));
	}

	private int getHeaderStart(String line) {
		return line.indexOf(' ') + 1;
	}

	private int getHeaderSize(String line) {
		int size = 1;
		int i = line.indexOf('+') + 1;
		while (i < line.length() && line.charAt(i) == '+' && size < 6) {
			i++;
			size++;
		}
		return size;
	}

	protected boolean isOrderedList(String line) {
		return line.matches(TwikiBrowserContentRenderer.ORDERED_LIST_MARKUP_REGEX);
	}

	protected char getListType(String line) {
		return line.trim().charAt(0);
	}

	protected int getListDepth(String line) {
		if (isOrderedList(line)) {
			return line.indexOf(TwikiBrowserContentRenderer.ORDERED_LIST_END_MARKER) / 3;
		}
		return line.indexOf(TwikiBrowserContentRenderer.UNORDERED_LIST_MARKUP) / 3;
	}

	protected boolean isHeader(String line) {
		return line.matches(TwikiBrowserContentRenderer.HEADER_MARKUP_REGEX);
	}

	protected boolean isList(String line) {
		return isUnorderedList(line) || isOrderedList(line);
	}

	private boolean isUnorderedList(String line) {
		return line.matches(TwikiBrowserContentRenderer.UNORDERED_LIST_MARKUP_REGEX);
	}

	protected String getListText(String line) {
		if (isOrderedList(line)) {
			return line.substring(line.indexOf(TwikiBrowserContentRenderer.ORDERED_LIST_END_MARKER) + 1).trim();
		}
		return line.substring(line.indexOf(TwikiBrowserContentRenderer.UNORDERED_LIST_MARKUP) + 1).trim();
	}

	protected boolean process(String line) {
		if (isVerbatim(line)) {
			processVerbatim();
			return true;
		} else if (line.trim().matches("^----*$")) {
			appendHR();
			return true;
		}
		return false;
	}

	private void processVerbatim() {
		getBuffer().append("<pre>");
		while (hasNextLine()) {
			String line = getNextLine();
			if (isEndVerbatim(line)) {
				break;
			}
			getBuffer().append(encode(line));
			if (!isEndVerbatim(peekNextLine())) {
				appendNewLine();
			}
		}
		getBuffer().append("</pre>");
	}

	private boolean isEndVerbatim(String line) {
		return line.toLowerCase().startsWith("</verbatim>");
	}

	private boolean isVerbatim(String line) {
		return line.toLowerCase().startsWith("<verbatim>");
	}

	protected String processTags(String line) {
		// enclose in white space for the regex that follow
		line = '\n' + line + '\n';

		// the following madness from TWiki's Render.pm
		line = line.replaceAll("([\\s\\(])==([^\\s]+?|[^\\s].*?[^\\s])==([\\s\\,\\.\\;\\:\\!\\?\\)])", "$1<code><b>$2</b></code>$3");
		line = line.replaceAll("([\\s\\(])__([^\\s]+?|[^\\s].*?[^\\s])__([\\s\\,\\.\\;\\:\\!\\?\\)])", "$1<strong><em>$2</em></strong>$3");
		line = line.replaceAll("([\\s\\(])\\*([^\\s]+?|[^\\s].*?[^\\s])\\*([\\s\\,\\.\\;\\:\\!\\?\\)])", "$1<strong>$2</strong>$3");
		line = line.replaceAll("([\\s\\(])_([^\\s]+?|[^\\s].*?[^\\s])_([\\s\\,\\.\\;\\:\\!\\?\\)])", "$1<em>$2</em>$3");
		line = line.replaceAll("([\\s\\(])=([^\\s]+?|[^\\s].*?[^\\s])=([\\s\\,\\.\\;\\:\\!\\?\\)])", "$1<code>$2</code>$3");

		// get rid of the enclosing white space we added above
		return line.substring(1, line.length() - 1);
	}

}