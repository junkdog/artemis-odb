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
import com.artemis.component.HitPoints;
import com.artemis.component.Velocity;
import com.artemis.component.Position;
import com.artemis.component.Complex;
import com.artemis.component.Asset;

@Wire(failOnNull=false)
public class ExtendedImpl implements Extended {
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
	private boolean _cullible_culled;
	private boolean _id_asset_path_;
	private boolean _id_culled_culled_;
	private boolean _id_hoho_x_y_;
	private boolean _id_pos_x_y_;
	private boolean _id_position_x_y_;
	private boolean _id_sprite_dummy_;
	private boolean _id_velocity_x_;
	private boolean _id_velocity_x_y_;
	private float _complex_x;
	private float _complex_y;
	private float _position_x;
	private float _position_y;
	private float _velocity_x;
	private float _velocity_y;
	private int _hitPoints_current;
	private java.lang.String _asset_path;
	private java.lang.String _sprite_dummy;
	
	// mappers
	private ComponentMapper<HitPoints> hitPointsMapper;
	private ComponentMapper<Asset> assetMapper;
	private ComponentMapper<Velocity> velocityMapper;
	private ComponentMapper<Position> positionMapper;
	private ComponentMapper<Cullible> cullibleMapper;
	private ComponentMapper<Complex> complexMapper;
	private ComponentMapper<Sprite> spriteMapper;
	
	public ExtendedImpl(World world) {
		this.world = world;
		world.inject(this);
		
		archetype = new ArchetypeBuilder()
			.add(Cullible.class)
			.add(Sprite.class)
			.add(HitPoints.class)
			.add(Velocity.class)
			.add(Position.class)
			.add(Complex.class)
			.add(Asset.class)
			.build(world);
	}
	
	private ExtendedImpl() {}
	
	@Override
	public Extended copy() {
		ExtendedImpl copy = new ExtendedImpl();
		copy.world = world;
		copy.archetype = archetype;
		copy._hitPoints_current = _hitPoints_current;
		
		world.inject(copy);
		
		return copy;
	}

	@Override
	public Extended tag(String tag) {
		_tag = true;
		this._tag_tag = tag;
		return this;
	}

	@Override
	public Extended group(String group) {
		_group = true;
		_groups.add(group);
		return this;
	}

	@Override
	public Extended group(String groupA, String... groups) {
		_groups.add(groupA);
		for (int i = 0; groups.length > i; i++) {
			_groups.add(groups[i]);
		}
		return this;
	}

	@Override
	public Extended group(String groupA, String groupB, String... groups) {
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

		if (_id_position_x_y_) {
			Position c = positionMapper.get(e);
			c.x = _position_x;
			c.y = _position_y;
			_id_position_x_y_ = false;
		}
		
		if (_id_velocity_x_y_) {
			Velocity c = velocityMapper.get(e);
			c.x = _velocity_x;
			c.y = _velocity_y;
			_id_velocity_x_y_ = false;
		}
		
		if (_id_velocity_x_) {
			Velocity c = velocityMapper.get(e);
			c.x = _velocity_x;
			_id_velocity_x_ = false;
		}
		
		if (_id_asset_path_) {
			Asset c = assetMapper.get(e);
			c.path = _asset_path;
			_id_asset_path_ = false;
		}
		
		if (_id_culled_culled_) {
			Cullible c = cullibleMapper.get(e);
			c.culled = _cullible_culled;
			_id_culled_culled_ = false;
		}
		
		if (_id_sprite_dummy_) {
			Sprite c = spriteMapper.get(e);
			c.dummy = _sprite_dummy;
			_id_sprite_dummy_ = false;
		}
		
		{
			HitPoints c = hitPointsMapper.get(e);
			c.current = _hitPoints_current;
		}
		
		if (_id_pos_x_y_) {
			Complex c = complexMapper.get(e);
			c.pos(_complex_x, _complex_y);
			_id_pos_x_y_ = false;
		}
		
		if (_id_hoho_x_y_) {
			Complex c = complexMapper.get(e);
			c.vel(_complex_x, _complex_y);
			_id_hoho_x_y_ = false;
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
	public Extended hitPoints(int current) {
		if (_sealed) {
			String err = "hitPoints are stickied, unable to change after creating " +
					"first entity. See copy().";
			throw new IllegalArgumentException(err);
		}
		_hitPoints_current = current;
		return this;
	}

	@Override
	public Extended position(float x, float y) {
		_id_position_x_y_ = true;
		_position_x = x;
		_position_y = y;
		return this;
	}
	@Override
	public Extended velocity(float x, float y) {
		_id_velocity_x_y_ = true;
		_velocity_x = x;
		_velocity_y = y;
		return this;
	}
	@Override
	public Extended velocity(float x) {
		_id_velocity_x_ = true;
		_velocity_x = x;
		return this;
	}
	@Override
	public Extended asset(java.lang.String path) {
		_id_asset_path_ = true;
		_asset_path = path;
		return this;
	}
	@Override
	public Extended culled(boolean culled) {
		_id_culled_culled_ = true;
		_cullible_culled = culled;
		return this;
	}
	@Override
	public Extended sprite(java.lang.String dummy) {
		_id_sprite_dummy_ = true;
		_sprite_dummy = dummy;
		return this;
	}
	@Override
	public Extended pos(float x, float y) {
		_id_pos_x_y_ = true;
		_complex_x = x;
		_complex_y = y;
		return this;
	}
	@Override
	public Extended hoho(float x, float y) {
		_id_hoho_x_y_ = true;
		_complex_x = x;
		_complex_y = y;
		return this;
	}
}
