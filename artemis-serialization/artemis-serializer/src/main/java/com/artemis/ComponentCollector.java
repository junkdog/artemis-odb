package com.artemis;

import com.artemis.annotations.Transient;
import com.artemis.io.SaveFileFormat;
import com.artemis.utils.BitVector;

import java.util.*;

/**
 * During saving, this class is responsible for collecting all used
 * component types which aren't annotated with {@link Transient}.
 */
public class ComponentCollector {
	private BitVector componentIds = new BitVector();
	private Set<Class<Component>> referencedComponents = new HashSet<Class<Component>>();

	private World world;

	public ComponentCollector(World world) {
		this.world = world;
	}

	public void preWrite(SaveFileFormat save) {
		componentIds.clear();
		referencedComponents.clear();

		inspectComponentTypes(save);
		extractComponents(save);
	}

	protected void extractComponents(SaveFileFormat save) {
		ComponentManager cm = world.getComponentManager();
		Map<Class<? extends Component>, String> lookup = save.componentIdentifiers.typeToName;

		Set<String> names = new HashSet<String>();
		BitVector bs = componentIds;
		SaveFileFormat.ComponentIdentifiers identifiers = save.componentIdentifiers;
		for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) {
			Class<? extends Component> type = cm.typeFactory.getTypeFor(i).getType();
			lookup.put(type, resolveNameId(names, type));
			if (identifiers.typeToId.get(type) == null) {
				identifiers.typeToId.put(type, lookup.size());
				identifiers.idToType.put(lookup.size(), type);
			}
		}
	}

	private String resolveNameId(Set<String> existing, Class<? extends Component> type) {
		String name = type.getSimpleName();
		if (existing.contains(name)) {
			int index = 2;
			while(existing.contains(name + "_" + index++));
			name += "_" + (index - 1);
		}

		existing.add(name);
		return name;
	}

	protected void inspectComponentTypes(SaveFileFormat save) {
		ComponentManager cm = world.getComponentManager();

		int[] ids = save.entities.getData();
		for (int i = 0, s = save.entities.size(); s > i; i++)
			componentIds.or(cm.componentBits(ids[i]));
	}
}
