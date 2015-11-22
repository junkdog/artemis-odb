package com.artemis.meta;

import java.io.InputStream;

import com.artemis.component.*;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
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
		
		assertEquals(2, scan1.fields().size());
		assertEquals("F", scan1.fields().get(1).desc);
		assertEquals(Opcodes.ACC_PRIVATE, scan1.fields().get(1).access);
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

	@Test
	public void read_default_boolean() throws Exception {
		ClassMetadata scan = scan(DefaultBoolean.class);

		assertEquals(true, scan.field("set").value);
		assertEquals(false, scan.field("unset").value);
	}

	@Test
	public void read_default_decimal_values() throws Exception {
		ClassMetadata scan = scan(DefaultDecimal.class);

		assertEquals(0, scan.field("f0").value);
		assertEquals(0, scan.field("d0").value);
		assertEquals(123f, scan.field("f123").value);
		assertEquals(0.00123f, scan.field("d123").value);
	}

	@Test
	public void read_default_integer_values() throws Exception {
		ClassMetadata scan = scan(DefaultInteger.class);

		assertEquals(0x12, scan.field("b012").value);
		assertEquals(0x0, scan.field("b0").value);
		assertEquals(0, scan.field("i0").value);
		assertEquals(543, scan.field("i543").value);
		assertEquals(-1, scan.field("l1").value);
	}

	@Test
	public void read_default_string() throws Exception {
		ClassMetadata scan = scan(DefaultString.class);

		assertEquals("hi", scan.field("hi").value);
		assertEquals("bye", scan.field("bye").value);
		assertEquals(null, scan.field("nullString").value);
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
