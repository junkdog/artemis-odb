package com.artemis.managers;

import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.World;
import com.artemis.annotations.Wire;
import com.artemis.io.SaveFileFormat;
import com.artemis.utils.IntBag;

import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;

@Wire
public class WorldSerializationManager extends Manager {
	private static final String TAG = WorldSerializationManager.class.getSimpleName();
	private ArtemisSerializer<?> backend;


	public WorldSerializationManager() {
	}

	@Override
	protected void initialize() {
	}

	public void setSerializer(ArtemisSerializer<?> backend) {
		this.backend = backend;
	}

	public <T extends SaveFileFormat> T load(InputStream is, Class<T> format) {
		return backend.load(is, format);
//		return null;
	}

	public void save(Writer writer, SaveFileFormat format) {
		if (backend == null) {
			throw new RuntimeException("Missing ArtemisSerializer, see #setBackend.");
		}

		world.inject(format);
		backend.save(writer, format);
	}

	public static abstract class ArtemisSerializer<T> {
		protected World world;

		protected ArtemisSerializer(World world) {
			this.world = world;
		}

		protected final void save(Writer writer, IntBag entities) {
			save(writer, new SaveFileFormat(entities));
		}

		public abstract ArtemisSerializer register(Class<?> type, T serializer);
		protected abstract void save(Writer writer, SaveFileFormat format);
		protected abstract <T extends SaveFileFormat> T load(InputStream is, Class<T> format);
	}
}
