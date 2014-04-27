package com.artemis.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class CollectionsUtil {
	private CollectionsUtil() {}
	
	public static <T> Set<Class<? extends T>> filter(Collection<Class<? extends T>> source, String packageFilter) {
		Set<Class<? extends T>> filtered = new HashSet<Class<? extends T>>();
		for (Class<? extends T> klazz : source) {
			if (klazz.getName().startsWith(packageFilter))
				filtered.add(klazz);
		}
		return filtered;
	}
}
