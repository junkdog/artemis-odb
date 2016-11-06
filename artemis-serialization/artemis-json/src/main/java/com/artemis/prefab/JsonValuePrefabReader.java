package com.artemis.prefab;

import com.esotericsoftware.jsonbeans.JsonReader;
import com.esotericsoftware.jsonbeans.JsonValue;

import java.io.InputStream;

public class JsonValuePrefabReader implements PrefabReader<JsonValue> {
	private JsonValue data;

	@Override
	public void initialize(String path) {
		if (!path.startsWith("/"))
			path = "/" + path;

		InputStream is = JsonValuePrefabReader.class.getResourceAsStream(path);
		data = new JsonReader().parse(is);
	}

	@Override
	public JsonValue getData() {
		return data;
	}
}
