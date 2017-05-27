package com.artemis.managers;

import com.artemis.*;
import com.artemis.annotations.Wire;
import com.artemis.component.*;
import com.artemis.component.render.TextureReference;
import com.artemis.io.JsonArtemisSerializer;
import com.artemis.io.KryoArtemisSerializer;
import com.artemis.io.SaveFileFormat;
import com.artemis.utils.IntBag;
import com.artemis.utils.Vector2;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

@Wire(injectInherited = true)
public class KryoEntityReferencesTest extends AbstractEntityReferencesTest {
	public KryoEntityReferencesTest() {
		super("/level_3.bin");
	}
	@Override
	protected WorldSerializationManager.ArtemisSerializer<?> createBackend(World world) {
		return new KryoArtemisSerializer(world);
	}
}
