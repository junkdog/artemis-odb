package com.artemis.prefab;

import com.artemis.ComponentMapper;
import com.artemis.World;
import com.artemis.annotations.PrefabData;
import com.artemis.component.ComponentX;
import com.artemis.io.SaveFileFormat;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;

@PrefabData("prefab/some_prefab.json")
public class SomePrefab extends Prefab {
	private ComponentMapper<ComponentX> componentXMapper;

	public SomePrefab(World world, FileHandleResolver resolver) {
		super(world, resolver);
	}

	public SaveFileFormat create(String text) {
		SaveFileFormat l = create();
		componentXMapper.get(l.get("whatever").getId()).text = text;

		return l;
	}
}
