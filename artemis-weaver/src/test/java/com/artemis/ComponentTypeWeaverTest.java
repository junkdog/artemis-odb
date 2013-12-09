package com.artemis;

import static com.artemis.Transformer.transform;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.artemis.component.ComponentToWeave;
import com.artemis.component.PackedToBeB;
import com.artemis.component.PooledForced;
import com.artemis.component.PooledNotForced;
import com.artemis.meta.ClassMetadata;
import com.artemis.meta.ClassMetadata.GlobalConfiguration;
import com.artemis.meta.ClassMetadata.WeaverType;

@SuppressWarnings("static-method")
public class ComponentTypeWeaverTest {
	
	@Before
	public void init() {
		GlobalConfiguration.enabledPooledWeaving = true;
	}
	
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
	
	@Test @Ignore // rewrite to match actual waeving
	public void pooled_disbled_weaving_test() throws Exception {
		GlobalConfiguration.enabledPooledWeaving = false;
		
		ClassMetadata meta = transform(PooledNotForced.class);
		assertFalse(meta.foundReset); 
		assertEquals("com/artemis/Component", meta.superClass); 
	}
	
	@Test
	public void pooled_forced_weaving_test() throws Exception {
		GlobalConfiguration.enabledPooledWeaving = false;
		
		ClassMetadata meta = transform(PooledForced.class);
		assertEquals(WeaverType.NONE, meta.annotation);
		assertTrue(meta.foundReset); 
		assertEquals("com/artemis/PooledComponent", meta.superClass); 
	}
}
