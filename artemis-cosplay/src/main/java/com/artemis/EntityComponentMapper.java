package com.artemis;

/**
 * @author Daan van Yperen
 */
public class EntityComponentMapper<A extends Component> extends CosplayComponentMapper<A, Entity> {
    public EntityComponentMapper(Class<A> type, World world) {
        super(type, world);
    }
}
