package com.artemis.component;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import java.util.*;

@PooledWeaver
public class PooledCollections extends Component {
	public Array<String> array = new Array<String>();
	public Bag<String> bag = new Bag<String>();
	public ArrayList<String> arrayList = new ArrayList<String>();
	public HashMap<String, String> hashMap = new HashMap<String, String>();
	public HashSet<String> hashSet = new HashSet<String>();
	public IntBag intBag = new IntBag();
	public List<String> list = new ArrayList<String>();
	public Map<String, String> map = new HashMap<String, String>();
	public ObjectMap<String, String> objectMap = new ObjectMap<String, String>();

	public void setAll() {
		array.add("array");
		bag.add("bag");
		hashSet.add("hashSet");
		arrayList.add("arrayList");
		list.add("list");
		hashMap.put("key", "map");
		intBag.add(1);
		map.put("lock", "unlocked");
		objectMap.put("obj", "map");
	}

	public void clearAll() {
		array.clear();
		bag.clear();
		hashSet.clear();
		arrayList.clear();
		list.clear();
		hashMap.clear();
		intBag.clear();
		map.clear();
		objectMap.clear();
	}
}
