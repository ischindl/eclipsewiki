package com.teaminabox.eclipse.wiki.editors;

import java.util.ArrayList;

public final class History {

	private ArrayList	items;

	private int			location;

	public History() {
		items = new ArrayList();
		location = -1;
	}

	public void add(Object object) {
		location++;
		int numberToRemove = items.size() - location;
		for (int i = 0; i < numberToRemove; i++) {
			items.remove(location);
		}
		items.trimToSize();
		items.add(object);
	}

	public int size() {
		return items.size();
	}

	public int getLocation() {
		return location;
	}

	public Object back() {
		location--;
		Object object = items.get(location);
		return object;
	}

	public Object next() {
		location++;
		return items.get(location);
	}

	public boolean hasPrevious() {
		return location > 0;
	}

	public boolean hasNext() {
		return location < items.size() - 1;
	}

	public Object getCurrent() {
		return items.get(location);
	}

	public String toString() {
		return "Location: " + location + ", " + items.toString();
	}

}
