package com.artemis;

import static com.artemis.Transformer.transform;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.artemis.component.PackedFields;
import com.artemis.meta.ClassMetadata;
import com.artemis.meta.ClassMetadata.WeaverType;

@SuppressWarnings("static-method")
public class PackedWithFieldAccess {

	@Test
	public void packed_direct_field_access() throws Exception {
		ClassMetadata meta = transform(PackedFields.class);
		assertEquals(WeaverType.NONE, meta.annotation);
		assertTrue(meta.foundReset); 
		assertTrue(meta.foundEntityFor);
		assertEquals("com/artemis/PackedComponent", meta.superClass); 
	}
}
