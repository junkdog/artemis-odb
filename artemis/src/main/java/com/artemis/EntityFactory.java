package com.artemis;


public interface EntityFactory<T> {
	T copy();
	T tag(String tag);
	T group(String group);
	T group(String groupA, String... groups);
	T group(String groupA, String groupB, String... groups);
	Entity create();
}
