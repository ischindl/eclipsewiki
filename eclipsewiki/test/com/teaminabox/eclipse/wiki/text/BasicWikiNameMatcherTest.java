package com.teaminabox.eclipse.wiki.text;

public final class BasicWikiNameMatcherTest extends AbstractTextRegionMatcherTest {

	private static final TextRegionTestBean[]	ACCEPTABLE_CASES	= new TextRegionTestBean[] { new TextRegionTestBean("WikiWord", new WikiNameTextRegion("WikiWord")), new TextRegionTestBean("WikiWord etc.", new WikiNameTextRegion("WikiWord")), new TextRegionTestBean("WikiWord.", new WikiNameTextRegion("WikiWord")), new TextRegionTestBean("WikiWord123", new WikiNameTextRegion("WikiWord123")), new TextRegionTestBean("WikiWord AnotherWikiWord", new WikiNameTextRegion("WikiWord")) };

	private static final String[]				UNACCEPTABLE_TEXT	= new String[] { "wikiword stuff", "com.canon.cre.ciki.Ciki com.capco.estp.context.SystemContext" };

	public BasicWikiNameMatcherTest(String name) {
		super(name);
	}

	protected TextRegionMatcher getMatcher() {
		return new BasicWikiNameMatcher();
	}

	protected String[] getUnacceptableText() {
		return BasicWikiNameMatcherTest.UNACCEPTABLE_TEXT;
	}

	protected TextRegionTestBean[] getAcceptableCases() {
		return BasicWikiNameMatcherTest.ACCEPTABLE_CASES;
	}

}
