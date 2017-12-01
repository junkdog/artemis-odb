package com.artemis.managers;

import com.artemis.EntitySubscription;
import com.artemis.annotations.Wire;
import com.artemis.io.SaveFileFormat;
import com.artemis.utils.IntBag;

/**
 * @author Daan van Yperen
 */
public class CustomSaveFormat extends SaveFileFormat {
	public AbstractCustomWorldSerializationManagerTest.SerializedSystem serialized;
	public AbstractCustomWorldSerializationManagerTest.DummySegment noSerializer;

	public CustomSaveFormat(EntitySubscription es, String dummyString, int dummyNumber) {
		super(es);
		noSerializer = new AbstractCustomWorldSerializationManagerTest.DummySegment(dummyString, dummyNumber);
	}

	public CustomSaveFormat() {
		super((IntBag) null);
	}
}
