package com.artemis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import com.artemis.component.ComponentToWeave;
import com.artemis.component.PackedToBeB;
import com.artemis.meta.ClassMetadata;
import com.artemis.weaver.ComponentTypeWeaver;

public class ComponentTypeWeaverTest {

	private ClassReader cr;
	private ClassMetadata crScan;

	@Test
	public void pooled_weaver_test() throws Exception {
		ClassMetadata meta = setup(ComponentToWeave.class);
		assertTrue(meta.foundReset); 
		assertFalse(meta.foundEntityFor);
		assertEquals("com/artemis/PooledComponent", meta.superClass); 
	}
	
	@Test
	public void packed_weaver_test() throws Exception {
		ClassMetadata meta = setup(PackedToBeB.class);
		assertTrue(meta.foundReset); 
		assertTrue(meta.foundEntityFor);
		assertEquals("com/artemis/PackedComponent", meta.superClass); 
	}
	
	private ClassMetadata setup(Class<?> klazz) throws Exception {
		cr = Weaver.classReaderFor(getClass().getResourceAsStream("/" + klazz.getName().replace('.', '/') + ".class"));
		crScan = Weaver.scan(cr);
		
		ComponentTypeWeaver weaver = new ComponentTypeWeaver(null, cr, crScan);
		weaver.call();
		
		ClassWriter cw = weaver.getClassWriter();
		assertEquals("", ClassUtil.verifyClass(cw));
		
		return Weaver.scan(new ClassReader(cw.toByteArray()));
	}
}
