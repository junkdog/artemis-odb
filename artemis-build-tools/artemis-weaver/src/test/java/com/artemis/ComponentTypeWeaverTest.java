package com.artemis;

import static com.artemis.Transformer.transform;
import static org.junit.Assert.*;

import java.io.InputStream;

import com.artemis.component.UnweavableComponent;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;

import com.artemis.component.ComponentToWeave;
import com.artemis.component.PooledForced;
import com.artemis.meta.ClassMetadata;
import com.artemis.meta.ClassMetadata.GlobalConfiguration;
import com.artemis.meta.ClassMetadata.WeaverType;

@SuppressWarnings("static-method")
public class ComponentTypeWeaverTest {
	
	@Before
	public void init() {
		GlobalConfiguration.enabledPooledWeaving = true;
	}

	@Test(expected = RuntimeException.class)
	public void dont_weave_unweavable_classes() throws Exception {
		transform(UnweavableComponent.class);
	}
	
	@Test
	public void pooled_weaver_test() throws Exception {
		ClassMetadata meta = Weaver.scan(transform(ComponentToWeave.class));
		assertEquals(WeaverType.NONE, meta.annotation);
		assertTrue(meta.foundReset); 
		assertEquals("com/artemis/PooledComponent", meta.superClass);
	}
	
	@Test
	public void pooled_forced_weaving_test() throws Exception {
		GlobalConfiguration.enabledPooledWeaving = false;
		ClassMetadata meta = Weaver.scan(transform(PooledForced.class));
		assertEquals(WeaverType.NONE, meta.annotation);
		assertTrue(meta.foundReset); 
		assertEquals("com/artemis/PooledComponent", meta.superClass); 
	}
}
