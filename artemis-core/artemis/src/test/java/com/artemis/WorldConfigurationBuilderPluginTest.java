package com.artemis;

import com.artemis.common.TestEntitySystemA;
import com.artemis.common.TestPluginA;
import com.artemis.common.TestPluginBDependentOnA;
import com.artemis.common.TestPluginCDependentOnA;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Daan van Yperen
 */
public class WorldConfigurationBuilderPluginTest {

    private WorldConfigurationBuilder builder;
    private ArtemisPlugin plugin;

    @Before
    public void setUp() {
        builder = new WorldConfigurationBuilder();
        plugin = mock(ArtemisPlugin.class);
    }


    @Test
    public void should_register_plugins() {
        builder.with(plugin).build();
        verify(plugin).setup(any(WorldConfigurationBuilder.class));
    }


    @Test
    public void should_register_last_minute_nested_plugins() {

        final ArtemisPlugin parentPlugin = new ArtemisPlugin() {
            @Override
            public void setup(WorldConfigurationBuilder b) {
                b.with(plugin);
            }
        };

        builder.with(parentPlugin).build();

        verify(plugin).setup(any(WorldConfigurationBuilder.class));
    }

    @Test(expected = WorldConfigurationException.class)
    public void should_ignore_double_plugins() {

        final ArtemisPlugin parentPlugin = new ArtemisPlugin() {
            @Override
            public void setup(WorldConfigurationBuilder b) {
                b.with(plugin, plugin);
                b.with(plugin);
            }
        };

        builder.with(parentPlugin).build();
    }

    @Test
    public void should_register_plugins_by_class() {
        builder.dependsOn(TestPluginA.class).build();
    }

    @Test
    public void should_support_multiple_dependencies_on_plugin() {
        builder.dependsOn(TestPluginBDependentOnA.class, TestPluginCDependentOnA.class).build();
    }

    @Test(expected = WorldConfigurationException.class)
    public void should_refuse_plugins_with_priority() {
        builder.dependsOn(WorldConfigurationBuilder.Priority.HIGH, TestEntitySystemA.class, TestPluginBDependentOnA.class).build();
    }

    @Test(expected = WorldConfigurationException.class)
    public void should_avoid_cyclic_dependencies() {
        final ArtemisPlugin parentPlugin = new ArtemisPlugin() {
            @Override
            public void setup(WorldConfigurationBuilder b) {
                b.with(this);
            }
        };
        builder.with(parentPlugin).build();
        // will get stuck in loop if failed.
    }

    public static abstract class AbstractPlugin implements ArtemisPlugin {
    }

    public static class AbstractPluginImpl extends AbstractPlugin {
        @Override
        public void setup(WorldConfigurationBuilder b) {
        }
    }

    @Test(expected = WorldConfigurationException.class)
    public void Should_report_missing_dependency_on_abstract_plugin() {
        builder.dependsOn(AbstractPlugin.class).build();
    }

    @Test
    public void Should_accept_implementation_to_abstract_dependency() {
        builder.dependsOn(AbstractPluginImpl.class).dependsOn(AbstractPlugin.class).build();
    }
}
