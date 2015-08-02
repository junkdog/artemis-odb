package com.artemis.component;

import java.nio.ByteBuffer;
import java.util.IdentityHashMap;
import java.util.Map;

import com.artemis.Entity;
import com.artemis.PackedComponent;
import com.artemis.PackedComponent.DisposedWithWorld;
import com.artemis.World;
import com.artemis.utils.Bag;

public class PackedWeaverReference extends PackedComponent implements DisposedWithWorld{

	private int $stride;
	private static final int $_SIZE_OF = 8;
	private static Map<World, Bag<PackedWeaverReference>> $store = new IdentityHashMap<World, Bag<PackedWeaverReference>>();
	private ByteBuffer $data = null;
	private World $world;
	
	public PackedWeaverReference(World world) {
		this.$world = world;
		Bag<PackedWeaverReference> instances = $store.get(world);
		if (instances != null) {
			$data = instances.get(0).$data;
		} else {
			$data = ByteBuffer.allocateDirect(128 * $_SIZE_OF);
			
			instances = new Bag<PackedWeaverReference>();
			$store.put(world, instances);
		}
		
		instances.add(this);
	}

	@Override
	protected void forEntity(int entityId) {
		this.$stride = $_SIZE_OF * entityId;
	}

	@Override
	protected void reset() {
		$data.putFloat($stride + 0, 0);
		$data.putFloat($stride + 4, 0);
	}
	
	
	@Override
	protected void ensureCapacity(int id) {
		int requested = (1 + id) * $_SIZE_OF;
		if ($data.capacity() < requested)
			$grow(2 * Math.max($data.capacity(), requested));
	}
	
	private void $grow(int capacity) {
		ByteBuffer newBuffer = ByteBuffer.allocateDirect(capacity);
		for (int i = 0, s = $data.capacity(); s > i; i++)
			newBuffer.put(i, $data.get(i));
		
		for (PackedWeaverReference ref : $store.get($world))
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
	
	public void x(float value) {
		$data.putFloat($stride + 0, value);
	}
	
	public void y(float value) {
		$data.putFloat($stride + 4, value);
	}
}
