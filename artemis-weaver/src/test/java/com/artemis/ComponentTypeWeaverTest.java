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
import com.artemis.meta.ClassMetadata.WeaverType;
import com.artemis.weaver.ComponentTypeWeaver;

public class ComponentTypeWeaverTest {

	@Test
	public void pooled_weaver_test() throws Exception {
		ClassMetadata meta = transform(ComponentToWeave.class);
		assertEquals(WeaverType.NONE, meta.annotation);
		assertTrue(meta.foundReset); 
		assertFalse(meta.foundEntityFor);
		assertEquals("com/artemis/PooledComponent", meta.superClass); 
	}
	
	@Test
	public void packed_weaver_test() throws Exception {
		ClassMetadata meta = transform(PackedToBeB.class);
		assertEquals(WeaverType.NONE, meta.annotation);
		assertTrue(meta.foundReset); 
		assertTrue(meta.foundEntityFor);
		assertEquals("com/artemis/PackedComponent", meta.superClass); 
	}
	
	private ClassMetadata transform(Class<?> klazz) throws Exception {
		ClassReader cr = Weaver.classReaderFor(getClass().getResourceAsStream("/" + klazz.getName().replace('.', '/') + ".class"));
		ClassMetadata meta = Weaver.scan(cr);
		
		ComponentTypeWeaver weaver = new ComponentTypeWeaver(null, cr, meta);
		weaver.call();
		
		ClassWriter cw = weaver.getClassWriter();
		assertEquals("", ClassUtil.verifyClass(cw));
		
		return Weaver.scan(new ClassReader(cw.toByteArray()));
	}
}
