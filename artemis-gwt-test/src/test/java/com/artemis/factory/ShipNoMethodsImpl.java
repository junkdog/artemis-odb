package com.artemis.factory;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.annotations.Wire;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import com.artemis.utils.Bag;
import com.artemis.component.Cullible;
import com.artemis.component.Sprite;

@Wire(failOnNull=false)
public class ShipNoMethodsImpl implements ShipNoMethods {
	// default fields
	private World world;
	private boolean _tag;
	private String _tag_tag;
	private boolean _group;
	private Bag<String> _groups = new Bag<String>();
	
	boolean _sealed;
	
	private TagManager tagManager;
	private GroupManager groupManager;
	private Archetype archetype;
	
	// component parameter fields
	
	// mappers
	
	public ShipNoMethodsImpl(World world) {
		this.world = world;
		world.inject(this);
		
		archetype = new ArchetypeBuilder()
			.add(Cullible.class)
			.add(Sprite.class)
			.build(world);
	}
	
	private ShipNoMethodsImpl() {}
	
	@Override
	public ShipNoMethods copy() {
		ShipNoMethodsImpl copy = new ShipNoMethodsImpl();
		copy.world = world;
		copy.archetype = archetype;
		
		world.inject(copy);
		
		return copy;
	}

	@Override
	public ShipNoMethods tag(String tag) {
		_tag = true;
		this._tag_tag = tag;
		return this;
	}

	@Override
	public ShipNoMethods group(String group) {
		_group = true;
		_groups.add(group);
		return this;
	}

	@Override
	public ShipNoMethods group(String groupA, String... groups) {
		_groups.add(groupA);
		for (int i = 0; groups.length > i; i++) {
			_groups.add(groups[i]);
		}
		return this;
	}

	@Override
	public ShipNoMethods group(String groupA, String groupB, String... groups) {
		_group = true;
		_groups.add(groupA);
		_groups.add(groupB);
		for (int i = 0; groups.length > i; i++) {
			_groups.add(groups[i]);
		}
		return this;
	}
	
	@Override
	public Entity create() {
		_sealed = true;
		
		Entity e = world.createEntity(archetype);

		if (_tag) {
			tagManager.register(_tag_tag, e);
			_tag = false;
		}
		
		if (_group) {
			for (int i = 0, s = _groups.size(); s > i; i++) {
				groupManager.add(e, _groups.get(i));
			}
			_group = false;
		}
		
		return e;
	}


}
