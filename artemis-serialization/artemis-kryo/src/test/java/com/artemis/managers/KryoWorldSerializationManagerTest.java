package com.artemis.managers;

import com.artemis.*;
import com.artemis.annotations.Wire;
import com.artemis.component.*;
import com.artemis.components.SerializationTag;
import com.artemis.io.KryoArtemisSerializer;
import com.artemis.io.KryoEntitySerializer;
import com.artemis.io.SaveFileFormat;
import com.artemis.utils.IntBag;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;

import static org.junit.Assert.*;

@Wire(failOnNull = false,injectInherited = true)
public class KryoWorldSerializationManagerTest extends AbstractWorldSerializationManagerTest {

	@Test
	public void serializer_save_not_entity_bag_success() throws Exception {
		setupWorld();

		asKryoBackend().register(NotEntityBagHolder.class, new KryoEntitySerializer.ComponentFieldSerializer<NotEntityBagHolder>((asKryoBackend()).getKryo(), NotEntityBagHolder.class) {
			@Override public void write (Kryo kryo, Output output, NotEntityBagHolder holder) {
				output.writeInt(holder.strings.size());
				for (String string : holder.strings) {
					output.writeString(string);
				}
			}

			@Override public NotEntityBagHolder read (Kryo kryo, Input input, Class aClass) {
				NotEntityBagHolder holder = edit.create(NotEntityBagHolder.class);
				int size = input.readInt();
				for (int i = 0; i < size; i++) {
					holder.strings.add(input.readString());
				}
				return holder;
			}
		});

		EntityEdit ee = world.createEntity().edit();
		NotEntityBagHolder holder = ee.create(NotEntityBagHolder.class);
		holder.strings.add("s1");
		holder.strings.add("s2");
		tags.register("reused-tag", ee.getEntityId());
		world.process();

		byte[] save = save(allEntities.getEntities());

		ByteArrayInputStream bais = new ByteArrayInputStream(save);
		SaveFileFormat l = wsm.load(bais, SaveFileFormat.class);

		world.process();

		Entity entity = world.getEntity(tags.getEntityId("reused-tag"));
		NotEntityBagHolder holder2 = entity.getComponent(NotEntityBagHolder.class);
		assertEquals(holder.strings.size(), holder2.strings.size());
		assertEquals(holder.strings.get(0), holder2.strings.get(0));
		assertEquals(holder.strings.get(1), holder2.strings.get(1));
	}

	private KryoArtemisSerializer asKryoBackend() {
		return (KryoArtemisSerializer)backend;
	}

	@Override
	protected WorldSerializationManager.ArtemisSerializer<?> createBackend(World world) {
		KryoArtemisSerializer result = new KryoArtemisSerializer(world);
		result.register(ComponentX.class);
		result.register(ComponentY.class);
		result.register(EntityBagHolder.class);
		result.register(EntityHolder.class);
		result.register(EntityIntBagHolder.class);
		result.register(NameComponent.class);
		result.register(NotEntityBagHolder.class);
		return result;
	}
}
