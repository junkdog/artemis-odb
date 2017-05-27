package com.artemis.utils;

import com.artemis.EntitySubscription;
import com.artemis.io.SaveFileFormat;
import com.artemis.managers.WorldSerializationManager;

import java.io.ByteArrayOutputStream;

public class SerializationUtil {

	public static String save(EntitySubscription subscription,
	                          WorldSerializationManager wsm)
			throws Exception {

		return save(subscription.getEntities(), wsm);
	}

	public static String save(IntBag entities,
	                          WorldSerializationManager manger)
			throws Exception {

		SaveFileFormat save = new SaveFileFormat(entities);
		ByteArrayOutputStream baos = new ByteArrayOutputStream(256);
		manger.save(baos, save);
		return baos.toString("UTF-8");
	}
}
