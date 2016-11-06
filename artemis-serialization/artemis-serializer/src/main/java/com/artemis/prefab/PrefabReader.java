package com.artemis.prefab;

/**
 * <p>Reads prefab data, receiving the <code>path</code>
 * from the prefab's {@link com.artemis.annotations.PrefabData}.</p>
 *
 * <p>The <code>artemis-odb-serializer-json-libgdx</code> and
 * <code>artemis-odb-serializer-json</code> artifacts bundle a default
 * <code>JsonValuePrefabReader</code>, which covers typical usage.</p>
 *
 * @see BasePrefab
 * @see com.artemis.annotations.PrefabData
 * @param <DATA> source data type.
 */
public interface PrefabReader<DATA> {
	void initialize(String path);
	DATA getData();
}
