package com.artemis.generator.strategy.e;

import com.artemis.generator.common.BuilderModelStrategy;
import com.artemis.generator.model.FluidTypes;
import com.artemis.generator.model.artemis.ArtemisModel;
import com.artemis.generator.model.type.MethodDescriptor;
import com.artemis.generator.model.type.ParameterizedTypeImpl;
import com.artemis.generator.model.type.TypeModel;
import com.artemis.generator.util.MethodBuilder;
import com.artemis.utils.ImmutableBag;

/**
 * Adds methods to access entities groups, and find entities by group.
 *
 * @author Daan van Yperen
 */
public class ComponentGroupStrategy implements BuilderModelStrategy {

    private MethodDescriptor createGroupSetter() {
        return
                new MethodBuilder(FluidTypes.E_TYPE, "group")
                        .parameter(String.class, "group")
                        .debugNotes("default group setter")
                        .statement("World w = mappers.getWorld()")
                        .statement("w.getSystem(com.artemis.managers.GroupManager.class).add(w.getEntity(entityId), group)")
                        .returnFluid()
                        .build();
    }

    private MethodDescriptor createGroupsSetter() {
        return
                new MethodBuilder(FluidTypes.E_TYPE, "groups")
                        .varArgs(true)
                        .parameter(String[].class, "groups")
                        .debugNotes("default groups setter")
                        .statement("for (int i = 0; groups.length > i; i++) { group(groups[i]); }")
                        .returnFluid()
                        .build();
    }


    private MethodDescriptor createGroupRemover() {
        return
                new MethodBuilder(FluidTypes.E_TYPE, "removeGroup")
                        .parameter(String.class, "group")
                        .debugNotes("default group remover")
                        .statement("World w = mappers.getWorld()")
                        .statement("w.getSystem(com.artemis.managers.GroupManager.class).remove(w.getEntity(entityId), group)")
                        .returnFluid()
                        .build();
    }

    private MethodDescriptor createGroupsRemover() {
        return
                new MethodBuilder(FluidTypes.E_TYPE, "removeGroups")
                        .varArgs(true)
                        .parameter(String[].class, "groups")
                        .debugNotes("default groups remover")
                        .statement("for (int i = 0; groups.length > i; i++) { removeGroup(groups[i]); }")
                        .returnFluid()
                        .build();
    }


    private MethodDescriptor createAllGroupRemover() {
        return
                new MethodBuilder(FluidTypes.E_TYPE, "removeGroups")
                        .debugNotes("default groups remover")
                        .statement("World w = mappers.getWorld()")
                        .statement("w.getSystem(com.artemis.managers.GroupManager.class).removeFromAllGroups(w.getEntity(entityId))")
                        .returnFluid()
                        .build();
    }

    private MethodDescriptor createGroupsGetter() {
        return
                new MethodBuilder(new ParameterizedTypeImpl(ImmutableBag.class, String.class), "groups")
                        .debugNotes("default groups getter")
                        .statement("World w = mappers.getWorld()")
                        .statement("return w.getSystem(com.artemis.managers.GroupManager.class).getGroups(w.getEntity(entityId))")
                        .build();
    }



    private MethodDescriptor createIsInGroup() {
        return
                new MethodBuilder(boolean.class, "isInGroup")
                        .parameter(String.class, "group")
                        .debugNotes("default group setter")
                        .statement("World w = mappers.getWorld()")
                        .statement("return w.getSystem(com.artemis.managers.GroupManager.class).isInGroup(w.getEntity(entityId), group)")
                        .build();
    }



    /**
     * static EBag E::withGroup(groupName)
     */
    private MethodDescriptor createStaticWithGroup() {
        return
                new MethodBuilder(FluidTypes.EBAG_TYPE, "withGroup")
                        .setStatic(true)
                        .parameter(String.class, "groupName")
                        .javaDoc("Get entities in group..\n@return {@code EBag} of entities in group. Returns empty bag if group contains no entities.")
                        .statement("if(_processingMapper==null) throw new RuntimeException(\"SuperMapper system must be registered before any systems using E().\");")
                        .statement("return new EBag((com.artemis.utils.IntBag)_processingMapper.getWorld().getSystem(com.artemis.managers.GroupManager.class).getEntityIds(groupName))")
                        .build();
    }

    @Override
    public void apply(ArtemisModel artemisModel, TypeModel model) {
        model.add(createGroupSetter());
        model.add(createGroupsSetter());
        model.add(createGroupRemover());
        model.add(createGroupsRemover());
        model.add(createAllGroupRemover());
        model.add(createGroupsGetter());
        model.add(createIsInGroup());
        model.add(createStaticWithGroup());
    }
}
