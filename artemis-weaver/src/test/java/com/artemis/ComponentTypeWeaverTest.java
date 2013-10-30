package com.artemis;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import com.artemis.component.ComponentToWeave;
import com.artemis.component.PackedToBeA;
import com.artemis.meta.ClassMetadata;
import com.artemis.weaver.ComponentTypeWeaver;

public class ComponentTypeWeaverTest {

	private ClassReader cr;
	private ClassMetadata crScan;

	@Test
	public void pooled_weaver_test() throws Exception {
		setup(ComponentToWeave.class);
	}
	
	@Test
	public void packed_weaver_test() throws Exception {
		setup(PackedToBeA.class);
	}
	
	private void setup(Class<?> klazz) throws Exception {
		cr = Weaver.classReaderFor(getClass().getResourceAsStream("/" + klazz.getName().replace('.', '/') + ".class"));
		crScan = Weaver.scan(cr);
		
		ComponentTypeWeaver weaver = new ComponentTypeWeaver(null, cr, crScan);
		weaver.call();
		
		ClassWriter cw = weaver.getClassWriter();
		assertEquals("", ClassUtil.verifyClass(cw));
	}
}
