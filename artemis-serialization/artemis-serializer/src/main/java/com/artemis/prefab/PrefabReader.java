package com.artemis.prefab;

public interface PrefabReader<DATA> {
	void initialize(String path);
	DATA getData();
}
