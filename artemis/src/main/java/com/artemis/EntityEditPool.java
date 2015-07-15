package com.artemis;

import java.util.BitSet;

import com.artemis.utils.Bag;

final class EntityEditPool {
	
	private final Bag<EntityEdit> pool = new Bag<EntityEdit>();
	private final World world;
	
	private WildBag<EntityEdit> edited;
	private WildBag<EntityEdit> alternateEdited;
	private final BitSet editedIds;
	
	EntityEditPool(World world) {
		this.world = world;
		
		edited = new WildBag<EntityEdit>();
		alternateEdited = new WildBag<EntityEdit>();
		editedIds = new BitSet();
	}
	
	boolean isEdited(Entity e) {
		return editedIds.get(e.id);
	}

	void processAndRemove(Entity e) {
		editedIds.set(e.id, false);
		EntityEdit edit = findEntityEdit(e, true);
		world.getEntityManager().updateCompositionIdentity(edit);
	}
	

	EntityEdit obtainEditor(Entity entity) {
		if (editedIds.get(entity.getId()))
			return findEntityEdit(entity, false);
		
		EntityEdit edit = entityEdit();
		editedIds.set(entity.getId());
		edited.add(edit);

		edit.entity = entity;
		edit.hasBeenAddedToWorld = world.getEntityManager().isActive(entity.getId());
		// since archetypes add components, we can't assume that an
		// entity has an empty bitset.
		// Note that editing an entity created by an archetype removes the performance
		// benefit of archetyped entity creation.
		BitSet bits = entity.getComponentBits();
		edit.componentBits.or(bits);

		return edit;
	}

	private EntityEdit entityEdit() {
		if (pool.isEmpty()) {
			return new EntityEdit(world);
		} else {
			EntityEdit edit = pool.removeLast();
			edit.componentBits.clear();
			edit.scheduledDeletion = false;
			return edit;
		}
	}
	
	private EntityEdit findEntityEdit(Entity entity, boolean remove) {
		// Since it's quite likely that already edited entities are called
		// repeatedly within the same scope, we start by first checking the last
		// element, before checking the rest.
		int last = edited.size() - 1;
		if (edited.get(last).entity == entity) {
			return remove ? edited.remove(last) : edited.get(last);
		}

		Object[] data = edited.getData();
		for (int i = 0; last > i; i++) {
			EntityEdit edit = (EntityEdit)data[i];
			if (!edit.entity.equals(entity))
				continue;

			return remove ? edited.remove(i) : edit;
		}
		
		throw new RuntimeException();
	}

	boolean processEntities() {
		int size = edited.size();
		if (size == 0)
			return false;
		
		Object[] data = edited.getData();
		editedIds.clear();
		edited.setSize(0);
		swapEditBags();
		
		World w = world;
		EntityManager em = w.getEntityManager();
		for (int i = 0; size > i; i++) {
			EntityEdit edit = (EntityEdit)data[i];
			em.updateCompositionIdentity(edit);
			addToPerformer(w, edit);
			
			pool.add(edit);
		}
		
		return true;
	}

	private static void addToPerformer(World w, EntityEdit edit) {
		if (edit.scheduledDeletion) {
			w.deleted.add(edit.entity);
		} else if (edit.hasBeenAddedToWorld) {
			w.changed.add(edit.entity);
		} else {
			w.added.add(edit.entity);
		}
	}

	private void swapEditBags() {
		WildBag<EntityEdit> tmp = edited;
		edited = alternateEdited;
		alternateEdited = tmp;
	}
}
