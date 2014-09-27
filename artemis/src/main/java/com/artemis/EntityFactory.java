package com.artemis;

public interface EntityFactory<T> {
	T copy();
	EntityFactory<T> tag(String tag);
	EntityFactory<T> group(String group);
	EntityFactory<T> group(String groupA, String... groups);
	EntityFactory<T> group(String groupA, String groupB, String... groups);
	Entity create();
}
