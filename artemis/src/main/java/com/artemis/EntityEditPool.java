package com.artemis;

import java.util.BitSet;

import com.artemis.utils.Bag;

final class EntityEditPool {
	
	private final Bag<EntityEdit> pool = new Bag<EntityEdit>();
	private final EntityManager em;
	
	private WildBag<EntityEdit> edited;
	private WildBag<EntityEdit> alternateEdited;
	private final BitSet editedIds;
	
	EntityEditPool(EntityManager entityManager) {
		em = entityManager;
		
		edited = new WildBag<EntityEdit>();
		alternateEdited = new WildBag<EntityEdit>();
		editedIds = new BitSet();
	}
	
	boolean isEdited(int entityId) {
		return editedIds.get(entityId);
	}

	void processAndRemove(int entityId) {
		editedIds.set(entityId, false);
		EntityEdit edit = findEntityEdit(entityId, true);
		em.updateCompositionIdentity(edit);
	}
	

	EntityEdit obtainEditor(int entityId) {
		if (editedIds.get(entityId))
			return findEntityEdit(entityId, false);
		
		EntityEdit edit = entityEdit();
		editedIds.set(entityId);
		edited.add(edit);

		edit.entityId = entityId;

		if (!em.isActive(entityId))
			throw new RuntimeException("Issued edit on deleted " + edit.entityId);

		// since archetypes add components, we can't assume that an
		// entity has an empty bitset.
		// Note that editing an entity created by an archetype removes the performance
		// benefit of archetyped entity creation.
		BitSet bits = em.componentBits(entityId);
		edit.componentBits.or(bits);

		return edit;
	}

	private EntityEdit entityEdit() {
		if (pool.isEmpty()) {
			return new EntityEdit(em.world);
		} else {
			EntityEdit edit = pool.removeLast();
			edit.componentBits.clear();
			edit.scheduledDeletion = false;
			return edit;
		}
	}
	
	private EntityEdit findEntityEdit(int entityId, boolean remove) {
		// Since it's quite likely that already edited entities are called
		// repeatedly within the same scope, we start by first checking the last
		// element, before checking the rest.
		int last = edited.size() - 1;
		if (edited.get(last).entityId == entityId) {
			return remove ? edited.remove(last) : edited.get(last);
		}

		Object[] data = edited.getData();
		for (int i = 0; last > i; i++) {
			EntityEdit edit = (EntityEdit)data[i];
			if (edit.entityId != entityId)
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
		
		World w = em.world;
		for (int i = 0; size > i; i++) {
			EntityEdit edit = (EntityEdit)data[i];
			em.updateCompositionIdentity(edit);
			addToPerformer(w, edit);
			
			pool.add(edit);
		}
		
		return true;
	}

	private void addToPerformer(World w, EntityEdit edit) {
		int id = edit.entityId;
		if (edit.scheduledDeletion) {
			w.deleted.set(id);
		} else {
			w.changed.set(id);
		}
	}

	private void swapEditBags() {
		WildBag<EntityEdit> tmp = edited;
		edited = alternateEdited;
		alternateEdited = tmp;
	}
}
