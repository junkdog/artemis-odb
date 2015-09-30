package com.artemis;

import java.lang.Class;import java.lang.Comparable;import java.lang.Object;import java.lang.Override;


/**
 * Artemis pieces with priority pending registration.
 *
 * @author Daan van Yperen
 * @see WorldConfigurationBuilder
 */
class ConfigurationElement<T> implements Comparable<ConfigurationElement<T>> {
	public final int priority;
	public final Class<?> itemType;
	public T item;

	public ConfigurationElement(T item, int priority) {
		this.item = item;
		itemType = item.getClass();
		this.priority = priority;
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
		return of(item, WorldConfigurationBuilder.Priority.NORMAL);
	}

	/** create instance of Registerable. */
	public static <T> ConfigurationElement<T> of(T item, int priority) {
		return new ConfigurationElement<T>(item, priority);
	}
}
