package com.artemis.generator.strategy.e;

import com.artemis.ComponentMapper;
import com.artemis.generator.common.BuilderModelStrategy;
import com.artemis.generator.model.FluidTypes;
import com.artemis.generator.model.artemis.ArtemisModel;
import com.artemis.generator.model.type.MethodDescriptor;
import com.artemis.generator.model.type.ParameterizedTypeImpl;
import com.artemis.generator.model.type.TypeModel;
import com.artemis.generator.util.MethodBuilder;
import com.artemis.utils.ImmutableBag;

/**
 * Generates group setters for E.
 *
 * @author Daan van Yperen
 */
public class ComponentGroupStrategy implements BuilderModelStrategy {

    private MethodDescriptor createGroupSetter() {
        return
                new MethodBuilder(FluidTypes.E_TYPE, "group")
                        .parameter(String.class, "group")
                        .debugNotes("default group setter")
                        .statement("world.getSystem(com.artemis.managers.GroupManager.class).add(id, group)")
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
                        .statement("world.getSystem(com.artemis.managers.GroupManager.class).remove(id, group)")
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
                        .statement("world.getSystem(com.artemis.managers.GroupManager.class).removeFromAllGroups(id)")
                        .returnFluid()
                        .build();
    }

    private MethodDescriptor createGroupsGetter() {
        return
                new MethodBuilder(new ParameterizedTypeImpl(ImmutableBag.class, String.class), "groups")
                        .debugNotes("default groups getter")
                        .statement("return world.getSystem(com.artemis.managers.GroupManager.class).getGroups(id)")
                        .build();
    }



    private MethodDescriptor createIsInGroup() {
        return
                new MethodBuilder(boolean.class, "isInGroup")
                        .parameter(String.class, "group")
                        .debugNotes("default group setter")
                        .statement("return world.getSystem(com.artemis.managers.GroupManager.class).isInGroup(id, group)")
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
    }
}
