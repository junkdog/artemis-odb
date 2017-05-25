package com.artemis.meta;

import java.io.InputStream;

import com.artemis.component.*;
import com.artemis.weaver.template.MultiEntityIdLink;
import com.artemis.weaver.template.MultiEntityLink;
import com.artemis.weaver.template.UniEntityIdLink;
import com.artemis.weaver.template.UniEntityLink;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;

import com.artemis.Entity;
import com.artemis.NullProfiler;
import com.artemis.World;
import com.artemis.meta.ClassMetadata.WeaverType;
import com.artemis.system.BeginEndSystem;
import com.artemis.system.NoBeginEndSystem;

import static org.junit.Assert.*;

@SuppressWarnings("static-method")
public class MetaScannerTest {
	
	private World world;

	@Before
	public void setup() {
		world = new World();
	}
	
	@Test @SuppressWarnings("unused")
	public void pooled_component_scanning() throws Exception {
		Entity e1 = world.createEntity();
		ComponentToWeave c1a = e1.edit().create(ComponentToWeave.class);
		PooledComponentWithReset c1b = e1.edit().create(PooledComponentWithReset.class);
		PooledComponentNotScanned c1c = e1.edit().create(PooledComponentNotScanned.class);

		ClassMetadata scan1 = scan(ComponentToWeave.class);
		ClassMetadata scan2 = scan(PooledComponentWithReset.class);
		ClassMetadata scan3 = scan(PooledComponentNotScanned.class);
		
		assertEquals(false, scan1.foundReset);
		assertEquals(WeaverType.POOLED, scan1.annotation);
		assertEquals(false, scan1.isPreviouslyProcessed);
		
		assertEquals(true, scan2.foundReset);
		assertEquals(WeaverType.POOLED, scan2.annotation);
		assertEquals(false, scan2.isPreviouslyProcessed);
		
		assertEquals(WeaverType.NONE, scan3.annotation);
	}
	
	@Test
	public void detect_begin_end() throws Exception {
		ClassMetadata scan1 = scan(NoBeginEndSystem.class);
		ClassMetadata scan2 = scan(BeginEndSystem.class);
		
		assertEquals(Type.getType(NullProfiler.class), scan1.profilerClass);
		assertTrue(scan1.profilingEnabled);
		assertTrue(scan1.foundInitialize);
		assertFalse(scan1.foundBegin);
		assertFalse(scan1.foundEnd);
		assertEquals(Type.getType(NullProfiler.class), scan2.profilerClass);
		assertFalse(scan2.profilingEnabled);
		assertFalse(scan2.foundInitialize);
		assertTrue(scan2.foundBegin);
		assertTrue(scan2.foundEnd);
	}

	@Test
	public void detect_entity_link_UniEntityLink() throws Exception {
		ClassMetadata meta = scan(UniEntityLink.class);

		assertEquals(1, meta.fields().size());
		FieldDescriptor fd = meta.fields().get(0);
		assertEquals(UniEntityLink.Mutator.class, fd.entityLinkMutator);
	}

	@Test
	public void detect_entity_link_UniEntityIdLink() throws Exception {
		ClassMetadata meta = scan(UniEntityIdLink.class);

		assertEquals(1, meta.fields().size());
		FieldDescriptor fd = meta.fields().get(0);
		assertEquals(UniEntityIdLink.Mutator.class, fd.entityLinkMutator);
	}

	@Test
	public void detect_entity_link_MultiEntityLink() throws Exception {
		ClassMetadata meta = scan(MultiEntityLink.class);

		assertEquals(1, meta.fields().size());
		FieldDescriptor fd = meta.fields().get(0);
		assertEquals(MultiEntityLink.Mutator.class, fd.entityLinkMutator);
	}

	@Test
	public void detect_entity_link_MultiEntityIdLink() throws Exception {
		ClassMetadata meta = scan(MultiEntityIdLink.class);

		assertEquals(1, meta.fields().size());
		FieldDescriptor fd = meta.fields().get(0);
		assertEquals(MultiEntityIdLink.Mutator.class, fd.entityLinkMutator);
	}

	@Test
	public void detect_entity_link_null() throws Exception {
		ClassMetadata meta = scan(ComponentToWeave.class);

		assertNotEquals(0, meta.fields().size());
		for (FieldDescriptor fd : meta.fields()) {
			assertNull(fd.entityLinkMutator);
		}
	}

	static ClassMetadata scan(Class<?> klazz) throws Exception {
		String classResource = "/" + klazz.getName().replace('.', '/') + ".class";
		
		InputStream stream = MetaScannerTest.class.getResourceAsStream(classResource);
		ClassReader cr = new ClassReader(stream);
		ClassMetadata info = new ClassMetadata();
		info.type = Type.getType(klazz);
		cr.accept(new MetaScanner(info), 0);
		stream.close();
		return info;
	}
}
