package com.teaminabox.eclipse.wiki.text;

public final class EscapedWikiWordMatcherTest extends AbstractTextRegionMatcherTest {
	
	private static final String	WIKI_WORD_PATTERN	= "\\!([A-Z][a-z]+){2,}[0-9]*";
	
	private static final TextRegionTestBean[]	ACCEPTABLE_CASES	= new TextRegionTestBean[] { 
		new TextRegionTestBean("!WikiWord", new BasicTextRegion("!WikiWord")), 
		new TextRegionTestBean("!WikiWord etc.", new BasicTextRegion("!WikiWord")), 
		new TextRegionTestBean("!WikiWord.", new BasicTextRegion("!WikiWord")), 
		new TextRegionTestBean("!WikiWord123", new BasicTextRegion("!WikiWord123")), 
		new TextRegionTestBean("!WikiWord AnotherWikiWord", new BasicTextRegion("!WikiWord")) };

	private static final String[]				UNACCEPTABLE_TEXT	= new String[] { "WikiWord" };

	public EscapedWikiWordMatcherTest(String name) {
		super(name);
	}

	protected TextRegionMatcher getMatcher() {
		return new EscapedWikiWordMatcher(WIKI_WORD_PATTERN, '!');
	}

	protected String[] getUnacceptableText() {
		return EscapedWikiWordMatcherTest.UNACCEPTABLE_TEXT;
	}

	protected TextRegionTestBean[] getAcceptableCases() {
		return EscapedWikiWordMatcherTest.ACCEPTABLE_CASES;
	}

}
