//#ifdef AGENT
		if (e instanceof Entity) {
			Entity entity = (Entity) e;
			if (entity.flyweight) {
				throw new RuntimeException(entity + "is flyweight, resolve "
						+ "to actual Entity instance with World#getEntity(int)");
            }
		}
//#endif