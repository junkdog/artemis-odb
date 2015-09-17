package com.artemis;

import java.lang.Class;import java.lang.Comparable;import java.lang.Object;import java.lang.Override;


/**
 * Artemis pieces with priority pending registration.
 *
 * @author Daan van Yperen
 * @see WorldConfigurationBuilder
 */
class ConfigurationElement<T> implements Comparable<ConfigurationElement<T>> {
	public final boolean passive;
	public final int priority;
	public final Class<?> itemType;
	public T item;

	public ConfigurationElement(T item, int priority, boolean passive) {
		this.item = item;
		itemType = item.getClass();
		this.priority = priority;
		this.passive = passive;
	}

	@Override
	public int compareTo(ConfigurationElement<T> o) {
		// Sort by priority descending.
		return o.priority - priority;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		return item.equals(((ConfigurationElement<?>) o).item);
	}

	@Override
	public int hashCode() {
		return item.hashCode();
	}

	/** create instance of Registerable. */
	public static <T> ConfigurationElement<T> of(T item) {
		return of(item, WorldConfigurationBuilder.Priority.NORMAL, false);
	}

	/** create instance of Registerable. */
	public static <T> ConfigurationElement<T> of(T item, int priority, boolean passive) {
		return new ConfigurationElement<T>(item, priority, passive);
	}

	/** create instance of Registerable. */
	public static <T> ConfigurationElement<T> of(T item, int priority) {
		return of(item, priority, false);
	}
}
