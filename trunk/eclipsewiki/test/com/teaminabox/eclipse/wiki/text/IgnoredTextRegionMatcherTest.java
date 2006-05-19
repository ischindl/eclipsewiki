package com.teaminabox.eclipse.wiki.text;

import com.teaminabox.eclipse.wiki.WikiConstants;

public final class IgnoredTextRegionMatcherTest extends AbstractTextRegionMatcherTest {

	private static final String					ACCEPTABLE_TEXT		= "IgnoreMe";
	private static final TextRegionTestBean[]	ACCEPTABLE_CASES	= new TextRegionTestBean[] { new TextRegionTestBean(ACCEPTABLE_TEXT + " stuff", new BasicTextRegion(ACCEPTABLE_TEXT)), new TextRegionTestBean(ACCEPTABLE_TEXT + " stuff", new BasicTextRegion(ACCEPTABLE_TEXT)), };

	public IgnoredTextRegionMatcherTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		create(IgnoredTextRegionMatcherTest.ACCEPTABLE_TEXT, WikiConstants.EXCLUDES_FILE);
	}

	protected TextRegionMatcher getMatcher() {
		return new IgnoredTextRegionMatcher();
	}

	protected String[] getUnacceptableText() {
		return new String[] { "IncludeMe" };
	}

	protected TextRegionTestBean[] getAcceptableCases() {
		return IgnoredTextRegionMatcherTest.ACCEPTABLE_CASES;
	}

}