package com.artemis;

import static com.artemis.WorldConfiguration.ENTITY_MANAGER_IDX;

/**
 * @author Daan van Yperen
 */
public class EntityWorld extends CosplayWorld<Entity> {

    public EntityWorld() {
        this(new WorldConfiguration());
    }

    public EntityWorld(WorldConfiguration configuration) {
        super(createConfiguration(configuration));
    }

    ;

    private static WorldConfiguration createConfiguration(WorldConfiguration configuration) {
        WorldConfiguration config = configuration
                .setEntityType(Entity.class);
        // TODO: move to configuration.
        config.systems.set(ENTITY_MANAGER_IDX, new EntityEntityManager(config.expectedEntityCount));
        return config;
    }

    @Override
    protected ComponentMapperFactory getComponentMapperFactory() {
        // TODO: move to configuration.
        return new ComponentMapperFactory() {
            @Override
            @SuppressWarnings("unchecked")
            public ComponentMapper instance(Class<? extends Component> type, World world) {
                return new EntityComponentMapper(type, world);
            }
        };
    }
}
