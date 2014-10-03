package com.artemis.reference;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.ParamArchTest.Asset;
import com.artemis.ParamArchTest.HitPoints;
import com.artemis.ParamArchTest.Position;
import com.artemis.ParamArchTest.Size;
import com.artemis.ParamArchTest.Velocity;
import com.artemis.World;
import com.artemis.annotations.Sticky;
import com.artemis.annotations.Wire;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import com.artemis.utils.Bag;

@Wire(failOnNull=false)
public class ShipImpl implements Ship {
	
	// default fields
	private World world;
	private boolean _tag;
	private String _tag_tag;
	private boolean _group;
	private Bag<String> _groups = new Bag<String>();

	boolean _sealed;
	
	// components
	// private boolean _hitpoints; // sticky
	private int _hitPoints_hitpoints;
	
	private boolean _size;
	private float _size_width;
	private float _size_height;
	
	
	private boolean _position;
	private float _position_x;
	private float _position_y;
	
	private boolean _asset;
	private String _asset_path;
	
	private boolean _velocity;
	private float _velocity_x;
	private float _velocity_y;
	
	// mappers and managers 
	private ComponentMapper<HitPoints> hitPointsMapper;
	private ComponentMapper<Position> positionMapper;
	private ComponentMapper<Size> sizeMapper;
	private ComponentMapper<Asset> assetMapper;
	private ComponentMapper<Velocity> velocityMapper;
	
	private TagManager tagManager;
	private GroupManager groupManager;
	private Archetype archetype;
	
	public ShipImpl(World world) {
		this.world = world;
		world.inject(this);
		
		archetype = new ArchetypeBuilder()
			.add(Sprite.class)
			.add(Cullible.class)
			.add(Position.class)
			.add(Velocity.class)
			.add(Asset.class)
			.add(Size.class)
			.add(HitPoints.class)
			.build(world);
	}
	
	private ShipImpl() {
		
	}
	
	@Override
	public Ship copy() {
		ShipImpl copy = new ShipImpl();
		copy.world = world;
		copy.archetype = archetype;
		copy._hitPoints_hitpoints = _hitPoints_hitpoints;
		
		world.inject(copy);
		
		return copy;
	}

	@Override
	public Ship tag(String tag) {
		_tag = true;
		this._tag_tag = tag;
		return this;
	}

	@Override
	public Ship group(String group) {
		_group = true;
		_groups.add(group);
		return this;
	}

	@Override
	public Ship group(String groupA, String... groups) {
		_groups.add(groupA);
		for (int i = 0; groups.length > i; i++) {
			_groups.add(groups[i]);
		}
		return this;
	}

	@Override
	public Ship group(String groupA, String groupB, String... groups) {
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
		if (_position) {
			Position c = positionMapper.get(e);
			c.x = _position_x;
			c.y = _position_y;
			_position = false;
		}
		
		if (_velocity) {
			Velocity c = velocityMapper.get(e);
			c.x = _velocity_x;
			c.y = _velocity_y;
			_velocity = false;
		}
		
		if (_asset) {
			Asset c = assetMapper.get(e);
			c.path =  _asset_path;
			_asset = false;
		}
		
		if (_size) {
			Size c = sizeMapper.get(e);
			c.width = _size_width;
			c.height = _size_height;
			
			_size = false;
		}
		
		{
			HitPoints c = hitPointsMapper.get(e);
			c.hitpoints = _hitPoints_hitpoints;
		}
		
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

	@Override
	public Ship position(float x, float y) {
		_position =  true;
		_position_x = x;
		_position_y = y;
		return this;
	}

	@Override
	public Ship velocity(float x, float y) {
		_velocity = true;
		_velocity_x = x;
		_velocity_y = y;
		return this;
	}

	@Override
	public Ship asset(String path) {
		_asset = true;
		_asset_path = path;
		return this;
	}

	@Override
	public Ship size(float width, float height) {
		_size = true;
		_size_width = width;
		_size_height = height;
		return this;
	}

	@Override @Sticky
	public Ship hitPoints(int hitpoints) {
		if (_sealed) {
			String err = "hitPoints are stickied, unable to change after creating " +
					"first entity. See copy().";
			throw new RuntimeException(err);
		}
		_hitPoints_hitpoints = hitpoints;
		return this;
	}
}
