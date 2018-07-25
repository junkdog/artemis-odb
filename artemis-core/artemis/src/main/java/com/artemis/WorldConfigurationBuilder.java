package com.artemis;

import com.artemis.annotations.UnstableApi;
import com.artemis.injection.CachedInjector;
import com.artemis.injection.FieldHandler;
import com.artemis.injection.FieldResolver;
import com.artemis.injection.InjectionCache;
import com.artemis.utils.Bag;
import com.artemis.utils.Sort;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.ReflectionException;

/**
 * World builder.
 * <p>
 * Allows convenient var-arg addition of systems, managers. Supports plugins.
 *
 * @author Daan van Yperen
 * @see WorldConfiguration
 */
public class WorldConfigurationBuilder {
    private Bag<ConfigurationElement<? extends BaseSystem>> systems;
    private Bag<ConfigurationElement<? extends FieldResolver>> fieldResolvers;
    private Bag<ConfigurationElement<? extends ArtemisPlugin>> plugins;

    private ArtemisPlugin activePlugin;
    private final InjectionCache cache;
    private SystemInvocationStrategy invocationStrategy;

    public WorldConfigurationBuilder() {
        reset();
        cache = InjectionCache.sharedCache.get();
    }

    /**
     * Assemble world with systems.
     * <p/>
     * Deprecated: World Configuration
     */
    public WorldConfiguration build() {
        appendPlugins();
        final WorldConfiguration config = new WorldConfiguration();
        registerSystems(config);
        registerFieldResolvers(config);
        registerInvocationStrategies(config);
        reset();
        return config;
    }

    private void registerInvocationStrategies(WorldConfiguration config) {
        if (invocationStrategy != null) {
            config.setInvocationStrategy(invocationStrategy);
        }
    }

    /**
     * Append plugin configurations.
     * Supports plugins registering plugins.
     */
    private void appendPlugins() {
        int i = 0;

        while (i < plugins.size()) {
            activePlugin = plugins.get(i).item;
            activePlugin.setup(this);
            i++;
        }
        activePlugin = null;
    }

    /**
     * add custom field handler with resolvers.
     */
    protected void registerFieldResolvers(WorldConfiguration config) {

        if (fieldResolvers.size() > 0) {
            Sort.instance().sort(fieldResolvers);
            // instance default field handler
            final FieldHandler fieldHandler = new FieldHandler(InjectionCache.sharedCache.get());

            for (ConfigurationElement<? extends FieldResolver> configurationElement : fieldResolvers) {
                fieldHandler.addFieldResolver(configurationElement.item);
            }

            config.setInjector(new CachedInjector().setFieldHandler(fieldHandler));
        }
    }

    /**
     * add systems to config.
     */
    private void registerSystems(WorldConfiguration config) {
        Sort.instance().sort(systems);
        for (ConfigurationElement<? extends BaseSystem> configurationElement : systems) {
            config.setSystem(configurationElement.item);
        }
    }

    /**
     * Reset builder
     */
    private void reset() {
        invocationStrategy = null;
        systems = new Bag<ConfigurationElement<? extends BaseSystem>>();
        fieldResolvers = new Bag<ConfigurationElement<? extends FieldResolver>>();
        plugins = new Bag<ConfigurationElement<? extends ArtemisPlugin>>();
    }

    /**
     * Add field resolver.
     *
     * @param fieldResolvers
     * @return this
     */
    public WorldConfigurationBuilder register(FieldResolver... fieldResolvers) {
        for (FieldResolver fieldResolver : fieldResolvers) {
            this.fieldResolvers.add(ConfigurationElement.of(fieldResolver));
        }
        return this;
    }

    /**
     * Add system invocation strategy.
     *
     * @param strategy strategy to invoke.
     * @return this
     */
    public WorldConfigurationBuilder register(SystemInvocationStrategy strategy) {
        this.invocationStrategy = strategy;
        return this;
    }

    /**
     * Specify dependency on systems/plugins.
     * <p/>
     * Managers track priority separate from system priority, and are always added before systems.
     *
     * 	Artemis will consider abstract plugin dependencies fulfilled when a concrete subclass has been registered
     * 	beforehand.
     *
     * @param types required systems.
     * @return this
     */
    public final WorldConfigurationBuilder dependsOn(Class... types) {
        return dependsOn(Priority.NORMAL, types);
    }

    /**
     * Specify dependency on systems/plugins.
     * <p/>
     *
     * @param types    required systems.
     * @param priority Higher priority are registered first. Not supported for plugins.
     * @return this
     * @throws WorldConfigurationException if unsupported classes are passed or plugins are given a priority.
     */
    @SuppressWarnings("unchecked")
    public final WorldConfigurationBuilder dependsOn(int priority, Class... types) {
        for (Class type : types) {
            try {
                switch (cache.getFieldClassType(type)) {
                    case SYSTEM:
                        dependsOnSystem(priority, type);
                        break;
                    default:
                        if (ClassReflection.isAssignableFrom(ArtemisPlugin.class, type)) {
                            if (priority != Priority.NORMAL) {
                                throw new WorldConfigurationException("Priority not supported on plugins.");
                            }
                            dependsOnPlugin(type);
                        } else {
                            throw new WorldConfigurationException("Unsupported type. Only supports systems.");
                        }
                }
            } catch (ReflectionException e) {
                throw new WorldConfigurationException("Unable to instance " + type + " via reflection.", e);
            }
        }
        return this;
    }

    protected void dependsOnSystem(int priority, Class<? extends BaseSystem> type) throws ReflectionException {
        if (!containsType(systems, type)) {
            this.systems.add(ConfigurationElement.of(ClassReflection.newInstance(type), priority));
        }
    }

    private void dependsOnPlugin(Class<? extends ArtemisPlugin> type) throws ReflectionException {

        if (ClassReflection.isAbstractClass(type)) {
            if (!anyAssignableTo(plugins, type)) {
                throw new WorldConfigurationException("Implementation of " + type + " expected but not found. Did you forget to include a plugin? (for example: logging-libgdx for logging-api)");
            }
        } else {
            if (!containsType(plugins, type)) {
                this.plugins.add(ConfigurationElement.of(ClassReflection.newInstance(type)));
            }
        }
    }

    /**
     * Register active system(s).
     * Only one instance of each class is allowed.
     * Use {@link #dependsOn} from within plugins whenever possible.
     *
     * @param systems  systems to add, order is preserved.
     * @param priority priority of added systems, higher priority are added before lower priority.
     * @return this
     * @throws WorldConfigurationException if registering the same class twice.
     */
    public WorldConfigurationBuilder with(int priority, BaseSystem... systems) {
        addSystems(priority, systems);
        return this;
    }

    /**
     * Register active system(s).
     * Only one instance of each class is allowed.
     * Use {@link #dependsOn} from within plugins whenever possible.
     *
     * @param systems systems to add, order is preserved.
     * @return this
     * @throws WorldConfigurationException if registering the same class twice.
     */
    public WorldConfigurationBuilder with(BaseSystem... systems) {
        addSystems(Priority.NORMAL, systems);
        return this;
    }


    /**
     * Add plugins to world.
     * <p/>
     * Upon build plugins will be called to register dependencies.
     * <p/>
     * Only one instance of each class is allowed.
     * Use {@link #dependsOn} from within plugins whenever possible.
     *
     * @param plugins Plugins to add.
     * @return this
     * @throws WorldConfigurationException if type is added more than once.
     */
    public WorldConfigurationBuilder with(ArtemisPlugin... plugins) {
        addPlugins(plugins);
        return this;
    }

    /**
     * helper to queue systems for registration.
     */
    private void addSystems(int priority, BaseSystem[] systems) {
        for (BaseSystem system : systems) {

            if (containsType(this.systems, system.getClass())) {
                throw new WorldConfigurationException("System of type " + system.getClass() + " registered twice. Only once allowed.");
            }

            this.systems.add(new ConfigurationElement<BaseSystem>(system, priority));
        }
    }

    /**
     * Check if bag of registerables contains any of passed type.
     *
     * @param items bag of registerables.
     * @param type  type to check for.
     * @return {@code true} if found {@code false} if none.
     */
    @SuppressWarnings("unchecked")
    private boolean containsType(Bag items, Class type) {
        for (ConfigurationElement<?> registration : (Bag<ConfigurationElement<?>>) items) {
            if (registration.itemType == type) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if bag of registerables contains any of passed type.
     *
     * @param items bag of possible subtypes.
     * @param type  supertype to check for.
     * @return {@code true} if found {@code false} if none.
     */
    @SuppressWarnings("unchecked")
    private boolean anyAssignableTo(Bag items, Class type) {
        for (ConfigurationElement<?> registration : (Bag<ConfigurationElement<?>>) items) {
            if (ClassReflection.isAssignableFrom(type, registration.itemType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Add new plugins.
     */
    private void addPlugins(ArtemisPlugin[] plugins) {
        for (ArtemisPlugin plugin : plugins) {

            if (containsType(this.plugins, plugin.getClass())) {
                throw new WorldConfigurationException("Plugin of type " + plugin.getClass() + " registered twice. Only once allowed.");
            }

            this.plugins.add(ConfigurationElement.of(plugin));
        }
    }

    /**
     * Guideline constants for priority, higher values has more priority. Will probably change.
     */
    @UnstableApi
    public static abstract class Priority {
        public static final int LOWEST = Integer.MIN_VALUE;
        public static final int LOW = -10000;
        public static final int OPERATIONS = -1000;
        public static final int NORMAL = 0;
        public static final int HIGH = 10000;
        public static final int HIGHEST = Integer.MAX_VALUE;
    }
}
