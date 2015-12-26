package com.artemis.managers;

import com.artemis.BaseSystem;
import com.artemis.World;
import com.artemis.io.InputStreamHelper;
import com.artemis.io.SaveFileFormat;
import com.artemis.utils.IntBag;

import java.io.*;

public class WorldSerializationManager extends BaseSystem {
	private static final String TAG = WorldSerializationManager.class.getSimpleName();
	private ArtemisSerializer<?> backend;
	public boolean alwaysLoadStreamMemory = true;


	@Override
	protected void processSystem() {
	}

	public void setSerializer(ArtemisSerializer<?> backend) {
		this.backend = backend;
	}

	public <T extends SaveFileFormat> T load(InputStream is, Class<T> format) {
		if (alwaysLoadStreamMemory || !InputStreamHelper.isMarkSupported(is)) {
			try {
				byte[] buf = new byte[32768];
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int read;
				while ((read = is.read(buf)) != -1) {
					baos.write(buf, 0, read);
				}
				is = new ByteArrayInputStream(baos.toByteArray());
				baos.close();
			} catch (IOException e) {
				throw new RuntimeException("Error copying inputstream", e);
			}
		}
		return backend.load(is, format);
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
