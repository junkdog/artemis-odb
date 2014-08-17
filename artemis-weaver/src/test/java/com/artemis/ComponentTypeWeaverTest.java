package com.artemis;

import static com.artemis.Transformer.transform;
import static com.artemis.Weaver.scan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;

import com.artemis.component.ComponentToWeave;
import com.artemis.component.IllegalComponent;
import com.artemis.component.PackedToBeB;
import com.artemis.component.PooledForced;
import com.artemis.component.PooledNotForced;
import com.artemis.component.SingletonTagComponent;
import com.artemis.meta.ClassMetadata;
import com.artemis.meta.ClassMetadata.GlobalConfiguration;
import com.artemis.meta.ClassMetadata.WeaverType;
import com.artemis.weaver.WeaverException;

@SuppressWarnings("static-method")
public class ComponentTypeWeaverTest {
	
	@Before
	public void init() {
		GlobalConfiguration.enabledPooledWeaving = true;
	}
	
	@Test
	public void pooled_weaver_test() throws Exception {
		ClassMetadata meta = Weaver.scan(transform(ComponentToWeave.class));
		assertEquals(WeaverType.NONE, meta.annotation);
		assertTrue(meta.foundReset); 
		assertFalse(meta.foundEntityFor);
		assertEquals("com/artemis/PooledComponent", meta.superClass); 
	}
	
	@Test
	public void packed_weaver_test() throws Exception {
		ClassMetadata meta = Weaver.scan(transform(PackedToBeB.class));
		assertEquals(WeaverType.NONE, meta.annotation);
		assertTrue(meta.foundReset); 
		assertTrue(meta.foundEntityFor);
		assertEquals("com/artemis/PackedComponent", meta.superClass); 
	}
	
	@Test
	public void singleton_tag_test() throws Exception {
		ClassMetadata meta = Weaver.scan(transform(SingletonTagComponent.class));
		assertEquals(WeaverType.NONE, meta.annotation);
		assertTrue(meta.foundReset); 
		assertTrue(meta.foundEntityFor);
		assertEquals(meta.fields.toString(), 0, meta.fields.size());
		assertEquals("com/artemis/PackedComponent", meta.superClass); 
	}
	
	@Test(expected=WeaverException.class)
	public void fail_weaver_test() throws Exception {
		transform(IllegalComponent.class);
	}
	
	@Test @Ignore // rewrite to match actual waeving
	public void pooled_disbled_weaving_test() throws Exception {
		GlobalConfiguration.enabledPooledWeaving = false;
		
		ClassMetadata meta = Weaver.scan(transform(PooledNotForced.class));
		assertFalse(meta.foundReset); 
		assertEquals("com/artemis/Component", meta.superClass); 
	}
	
	@Test
	public void pooled_forced_weaving_test() throws Exception {
		GlobalConfiguration.enabledPooledWeaving = false;
		
		ClassMetadata meta = Weaver.scan(transform(PooledForced.class));
		assertEquals(WeaverType.NONE, meta.annotation);
		assertTrue(meta.foundReset); 
		assertEquals("com/artemis/PooledComponent", meta.superClass); 
	}
	
	private static ClassMetadata scan(Class<?> klazz) {
		InputStream classStream = klazz.getResourceAsStream("/" + klazz.getName().replace('.', '/') + ".class");
		ClassReader cr = Weaver.classReaderFor(classStream);
		ClassMetadata meta = Weaver.scan(cr);
		meta.type = Type.getObjectType(cr.getClassName());
		
		return meta;
	}
}
