package com.teaminabox.eclipse.wiki.editors;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.WhitespaceRule;

import com.teaminabox.eclipse.wiki.renderer.RendererFactory;
import com.teaminabox.eclipse.wiki.text.TextRegionMatcher;
import com.teaminabox.eclipse.wiki.util.WikiWhitespaceDetector;

public final class WikiScanner extends RuleBasedScanner {

	public WikiScanner(WikiEditor wikiEditor) {
		ArrayList rules = new ArrayList();
		rules.add(new WhitespaceRule(new WikiWhitespaceDetector()));
		TextRegionMatcher[] matchers = RendererFactory.createContentRenderer().getScannerMatchers();
		for (int i = 0; i < matchers.length; i++) {
			matchers[i].setEditor(wikiEditor);
		}
		rules.addAll(Arrays.asList(matchers));
		setRules((IRule[]) rules.toArray(new IRule[rules.size()]));
	}
}