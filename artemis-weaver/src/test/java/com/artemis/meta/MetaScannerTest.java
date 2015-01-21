package com.artemis.meta;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.artemis.Entity;
import com.artemis.NullProfiler;
import com.artemis.World;
import com.artemis.component.ComponentToWeave;
import com.artemis.component.PackedToBeA;
import com.artemis.component.PackedToBeB;
import com.artemis.component.PooledComponentNotScanned;
import com.artemis.component.PooledComponentWithReset;
import com.artemis.meta.ClassMetadata.WeaverType;
import com.artemis.system.BeginEndSystem;
import com.artemis.system.NoBeginEndSystem;

@SuppressWarnings("static-method")
public class MetaScannerTest {
	
	private World world;

	@Before
	public void setup() {
		world = new World();
		world.initialize();
	}
	
	@Test @SuppressWarnings("unused")
	public void pooled_component_scanning() throws Exception {
		Entity e1 = world.createEntity();
		ComponentToWeave c1a = e1.createComponent(ComponentToWeave.class);
		PooledComponentWithReset c1b = e1.createComponent(PooledComponentWithReset.class);
		PooledComponentNotScanned c1c = e1.createComponent(PooledComponentNotScanned.class);
		e1.addToWorld();
		
		ClassMetadata scan1 = scan(ComponentToWeave.class);
		ClassMetadata scan2 = scan(PooledComponentWithReset.class);
		ClassMetadata scan3 = scan(PooledComponentNotScanned.class);
		
		assertEquals(false, scan1.foundReset);
		assertEquals(false, scan1.foundEntityFor);
		assertEquals(WeaverType.POOLED, scan1.annotation);
		assertEquals(false, scan1.isPreviouslyProcessed);
		
		assertEquals(true, scan2.foundReset);
		assertEquals(false, scan2.foundEntityFor);
		assertEquals(WeaverType.POOLED, scan2.annotation);
		assertEquals(false, scan2.isPreviouslyProcessed);
		
		assertEquals(WeaverType.NONE, scan3.annotation);
	}
	
	@Test
	public void packed_component_scanning() throws Exception {
		
		ClassMetadata scan1 = scan(PackedToBeA.class);
		ClassMetadata scan2 = scan(PackedToBeB.class);
		
		assertEquals(WeaverType.PACKED, scan1.annotation);
		assertEquals(true, scan1.foundEntityFor);
		assertEquals(false, scan1.foundReset);
		
		assertEquals(WeaverType.PACKED, scan2.annotation);
		assertEquals(false, scan2.foundEntityFor);
		assertEquals(false, scan2.foundReset);
	}
	
	@Test
	public void find_fields_and_methods() throws Exception {
		ClassMetadata scan1 = scan(PackedToBeB.class);
		ClassMetadata scan2 = scan(PackedToBeA.class);
		
		assertEquals(2, scan1.fields.size());
		assertEquals("F", scan1.fields.get(1).desc);
		assertEquals(Opcodes.ACC_PRIVATE, scan1.fields.get(1).access);
		assertEquals(1 /* default constructor*/, scan1.methods.size());
		
		assertEquals(2 /* default constructor*/, scan2.methods.size());
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
	
	static ClassMetadata scan(Class<?> klazz) throws Exception {
		String classResource = "/" + klazz.getName().replace('.', '/') + ".class";
		
		InputStream stream = MetaScannerTest.class.getResourceAsStream(classResource);
		ClassReader cr = new ClassReader(stream);
		ClassMetadata info = new ClassMetadata();
		cr.accept(new MetaScanner(info), 0);
		stream.close();
		return info;
	}
}
