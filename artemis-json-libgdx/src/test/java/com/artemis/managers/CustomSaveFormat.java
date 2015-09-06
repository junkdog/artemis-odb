package com.artemis.managers;

import com.artemis.EntitySubscription;
import com.artemis.annotations.Wire;
import com.artemis.io.SaveFileFormat;
import com.artemis.utils.IntBag;

/**
 * @author Daan van Yperen
 */
@Wire
public class CustomSaveFormat extends SaveFileFormat {
	public CustomJsonWorldSerializationManagerTest.SerializedSystem serialized;
	public CustomJsonWorldSerializationManagerTest.DummySegment noSerializer;

	public CustomSaveFormat(EntitySubscription es, String dummyString, int dummyNumber) {
		super(es);
		noSerializer = new CustomJsonWorldSerializationManagerTest.DummySegment(dummyString, dummyNumber);
	}

	public CustomSaveFormat() {
		super((IntBag) null);
	}
}
