package com.artemis.prefab;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class JsonValuePrefabReader implements PrefabReader<JsonValue> {
	private final FileHandleResolver resolver;
	private JsonValue data;

	public JsonValuePrefabReader(FileHandleResolver resolver) {
		this.resolver = resolver;
	}

	@Override
	public void initialize(String path) {
		data = new JsonReader().parse(resolver.resolve(path));
	}

	@Override
	public JsonValue getData() {
		return data;
	}
}
