package com.artemis.io;

import com.artemis.Component;

import java.io.Serializable;
import java.util.Comparator;

public class ComponentNameComparator implements Comparator<Component>, Serializable {
	@Override
	public int compare(Component o1, Component o2) {
		String name1 = o1.getClass().getSimpleName();
		String name2 = o2.getClass().getSimpleName();

		return name1.compareTo(name2);
	}
}
