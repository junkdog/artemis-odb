package com.artemis;

import static com.artemis.Transformer.transform;
import static com.artemis.Weaver.scan;
import static com.artemis.meta.ClassMetadataUtil.instanceFields;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.artemis.component.PackedToBeC;
import com.artemis.meta.ClassMetadata;
import com.artemis.meta.ClassMetadata.GlobalConfiguration;
import com.artemis.meta.ClassMetadata.WeaverType;
import com.artemis.meta.FieldDescriptor;

@SuppressWarnings("static-method")
public class IdeFriendlyPackingTest {

	@Before
	public void init() {
		GlobalConfiguration.ideFriendlyPacking = false;
	}
	
	@Test
	public void retain_fields_for_ide_friendliness() throws Exception {
		GlobalConfiguration.ideFriendlyPacking = true;
		
		ClassMetadata meta = scan(transform(PackedToBeC.class));
		List<FieldDescriptor> instanceFields = instanceFields(meta);
		assertEquals(instanceFields.toString(), 3, instanceFields.size()); // +1 for $offset
		assertEquals(WeaverType.NONE, meta.annotation);
		
		assertTrue(meta.foundReset); 
		assertTrue(meta.foundEntityFor);
		
		assertEquals("com/artemis/PackedComponent", meta.superClass); 
	}
	
	@Test
	public void remove_fields_no_ide() throws Exception {
		ClassMetadata meta = scan(transform(PackedToBeC.class));
		List<FieldDescriptor> instanceFields = instanceFields(meta);
		assertEquals(instanceFields.toString(), 1, instanceFields.size()); // $offset
		assertEquals(WeaverType.NONE, meta.annotation);
		
		assertTrue(meta.foundReset); 
		assertTrue(meta.foundEntityFor);
		
		assertEquals("com/artemis/PackedComponent", meta.superClass); 
	}
}
