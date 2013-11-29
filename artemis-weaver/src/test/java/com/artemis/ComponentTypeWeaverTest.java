package com.artemis;

import static com.artemis.Transformer.transform;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.artemis.component.ComponentToWeave;
import com.artemis.component.PackedToBeB;
import com.artemis.meta.ClassMetadata;
import com.artemis.meta.ClassMetadata.WeaverType;

@SuppressWarnings("static-method")
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
}
