package com.artemis.component;

import java.nio.ByteBuffer;
import java.util.IdentityHashMap;
import java.util.Map;

import com.artemis.Entity;
import com.artemis.PackedComponent;
import com.artemis.PackedComponent.DisposedWithWorld;
import com.artemis.World;
import com.artemis.util.Vec2f;
import com.artemis.utils.Bag;

public class TransPackedFloatReference2 extends PackedComponent implements DisposedWithWorld{

	private int $stride;
	private static final int $_SIZE_OF = 8;
	private static Map<World, Bag<TransPackedFloatReference2>> $store = new IdentityHashMap<World, Bag<TransPackedFloatReference2>>();
	private ByteBuffer $data = null;
	private World $world;
	
	public TransPackedFloatReference2(World world) {
		this.$world = world;
		Bag<TransPackedFloatReference2> instances = $store.get(world);
		if (instances != null) {
			$data = instances.get(0).$data;
		} else {
			$data = ByteBuffer.allocateDirect(128 * $_SIZE_OF);
			
			instances = new Bag<TransPackedFloatReference2>();
			$store.put(world, instances);
		}
		
		instances.add(this);
	}

	@Override
	protected void forEntity(Entity e) {
		this.$stride = $_SIZE_OF * e.getId();
	}

	@Override
	protected void reset() {
		$data.putFloat($stride + 0, 0);
		$data.putFloat($stride + 4, 0);
	}
	
	private void $grow()
	{
		ByteBuffer newBuffer = ByteBuffer.allocateDirect($data.capacity() * 2);
		for (int i = 0, s = $data.capacity(); s > i; i++)
			newBuffer.put(i, $data.get(i));
		
		for (TransPackedFloatReference2 ref : $store.get($world))
			ref.$data = newBuffer;
	}
	
	@Override
	public void free(World world) {
		$store.remove(world);
	}
	
	public float x() {
		return $data.getFloat($stride + 0);
	}
	
	public float y() {
		return $data.getFloat($stride + 4);
	}
	
	public TransPackedFloatReference2 x(float value) {
		$data.putFloat($stride + 0, value);
		return this;
	}
	
	public void y(float value) {
		$data.putFloat($stride + 4, value);
	}
	
	
	public void set(Vec2f v) {
		$data.putFloat($stride + 0, v.x());
		$data.putFloat($stride + 4, v.y);
	}

	@Override
	protected void ensureCapacity(int id) {
		if (($data.capacity() - $_SIZE_OF) <= (id * $_SIZE_OF))
			$grow();
	}
}
