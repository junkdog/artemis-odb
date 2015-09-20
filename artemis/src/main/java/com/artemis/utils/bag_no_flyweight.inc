//#ifdef AGENT
		if (e instanceof int) {
			int entity = (int) e;
			if (entity.flyweight) {
				throw new RuntimeException(entity + " is flyweight, resolve "
						+ "to actual int instance with World#getEntity(int)");
            }
		}
//#endif