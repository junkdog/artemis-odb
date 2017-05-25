package com.artemis.component;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;

import java.util.*;

@PooledWeaver
public class ComponentToWeave extends Component {
	private boolean hasBeenReset;
	private String s;

	public Bag<String> bag = new Bag<String>();
	public ArrayList<String> arrayList = new ArrayList<String>();
	public HashMap<String, String> hashMap = new HashMap<String, String>();
	public HashSet<String> hashSet = new HashSet<String>();
	public IntBag intBag = new IntBag();
	public List<String> list = new ArrayList<String>();
	public Map<String, String> map = new HashMap<String, String>();
}
