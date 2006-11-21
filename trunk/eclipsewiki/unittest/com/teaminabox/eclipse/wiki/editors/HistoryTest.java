package com.teaminabox.eclipse.wiki.editors;

import junit.framework.TestCase;

public final class HistoryTest extends TestCase {
	private History<String> history = new History<String>();

	public void testAdd() {
		history.add("foo");
		assertEquals("size", 1, history.size());
		assertEquals("location", 0, history.getLocation());
	}

	public void testAddingTheSameThingTwice() {
		history.add("foo");
		history.add("foo");
		assertEquals("size", 1, history.size());
		assertEquals("location", 0, history.getLocation());
	}

	public void testPrevious() {
		history.add("foo");
		history.add("bar");
		assertEquals("foo", history.back());
	}

	public void testHasPrevious() {
		assertFalse("nothing", history.hasPrevious());
		history.add("foo");
		assertFalse("one", history.hasPrevious());
	}

	public void testPreviousNothingAdded() {
		assertEquals("location", -1, history.getLocation());
		assertFalse("hasPrevious", history.hasPrevious());
	}

	public void testNext() {
		history.add("foo");
		history.add("bar");
		history.back();
		assertEquals("bar", history.next());
	}

	public void testNextNothingAdded() {
		assertEquals("location", -1, history.getLocation());
		assertFalse("hasNext", history.hasNext());
	}

	public void testFutureCleared() {
		history.add("foo");
		history.add("bar");
		history.add("doomed");
		history.back();
		history.back();
		history.add("newStuff");

		assertEquals("size", 2, history.size());
		assertEquals("newStuff", history.getCurrent());
	}
}
