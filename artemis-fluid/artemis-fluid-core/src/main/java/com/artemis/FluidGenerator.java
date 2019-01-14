package com.artemis;

import com.artemis.components.SerializationTag;
import com.artemis.generator.TypeModelGenerator;
import com.artemis.generator.collect.AbstractClassCollectStrategy;
import com.artemis.generator.collect.ReflectionsClassCollectStrategy;
import com.artemis.generator.generator.PoetSourceGenerator;
import com.artemis.generator.model.artemis.ArtemisModel;
import com.artemis.generator.model.artemis.ComponentDescriptor;
import com.artemis.generator.model.type.TypeModel;
import com.artemis.generator.strategy.components.ComponentsBaseStrategy;
import com.artemis.generator.strategy.components.ComponentsClassLibraryStrategy;
import com.artemis.generator.strategy.e.*;
import com.artemis.generator.strategy.supermapper.ComponentMapperFieldsStrategy;
import com.artemis.generator.strategy.supermapper.SuperMapperStrategy;
import com.artemis.generator.util.Log;
import com.artemis.generator.validator.TypeModelValidator;
import com.artemis.generator.validator.TypeModelValidatorException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.*;

/**
 * Fluid api class generator.
 *
 * @author Daan van Yperen
 */
public class FluidGenerator {

    private static final String FLUID_UTILITY_SOURCES_DIR = "/fluid-utility-sources";
    private static final String COM_ARTEMIS_MODULE_DIR = "com/artemis/";
    private static final Comparator<FieldProxyStrategy> SORT_BY_PRIORITY_DESC_FALLBACK_ON_NAME = new Comparator<FieldProxyStrategy>() {
        @Override
        public int compare(FieldProxyStrategy o1, FieldProxyStrategy o2) {
            final int p1 = o1.priority();
            final int p2 = o2.priority();
            return (p1 < p2) ? 1 : ((p1 > p2) ? -1 : o1.getClass().getName().compareTo(o2.getClass().getName()));
        }
    };

    /**
     * Generate fluid API files.
     * Finds all Component instances at given urls using reflection.
     *
     * @param urls              classpath urls to reflect over.
     * @param outputDirectory   source root.
     * @param log               output.
     * @param globalPreferences
     * @throws com.artemis.generator.validator.TypeModelValidatorException
     */
    public void generate(Set<URL> urls, File outputDirectory, Log log, FluidGeneratorPreferences globalPreferences) {
        final AbstractClassCollectStrategy collectStrategy = collectStrategy(urls);
        generate(collectStrategy.allComponents(), collectStrategy.allFieldProxyStrategies(), outputDirectory, log, globalPreferences);
    }


    private AbstractClassCollectStrategy collectStrategy(Set<URL> urls) {
        return new ReflectionsClassCollectStrategy(urls);
    }

    /**
     * Generate fluid API files.
     *
     * @param components           components to consider.
     * @param fieldProxyStrategies field proxy strategies to apply
     * @param outputDirectory      source root.
     * @param log                  output.
     * @param globalPreferences
     * @throws com.artemis.generator.validator.TypeModelValidatorException
     */
    public void generate(Collection<Class<? extends Component>> components, Collection<Class<? extends FieldProxyStrategy>> fieldProxyStrategies, File outputDirectory, Log log, FluidGeneratorPreferences globalPreferences) {

        if (fieldProxyStrategies == null || fieldProxyStrategies.isEmpty()) {
            log.error("Fluid API: No field proxy strategies found on class path, unable to add components fields to fluid interface!");
            log.error("Fluid API: Make sure net.onedaybeard.artemis:artemis-fluid-core is on your compile classpath for the plugin to find.");
            throw new RuntimeException("Field proxy strategy required but none found on (compile time) class path!");
        }

        if (components == null || components.isEmpty()) {
            log.error("Fluid API generation aborted, no components found on class path!");
            throw new RuntimeException("No components found on class path! Make sure your component classes are available.");
        }

        final ArtemisModel artemisModel = createArtemisModel(filterComponents(components, log), fieldProxyStrategies, globalPreferences, log);

        File outputArtemisModuleDirectory = new File(outputDirectory, COM_ARTEMIS_MODULE_DIR);
        outputArtemisModuleDirectory.mkdirs();

        generateFile(artemisModel, createSupermapperGenerator(globalPreferences), new File(outputArtemisModuleDirectory, "SuperMapper.java"), log);
        generateFile(artemisModel, createFluidInterfaceGenerator(globalPreferences), new File(outputArtemisModuleDirectory, "E.java"), log);
        generateFile(artemisModel, createComponentsGenerator(globalPreferences), new File(outputArtemisModuleDirectory, "C.java"), log);

        // deploy static utility classes that depend on E and/or SuperMapper. Do a clean when changing files!
        copyResourceIfMissing(getClass().getResource(FLUID_UTILITY_SOURCES_DIR + "/FluidEntityPlugin.java"), new File(outputArtemisModuleDirectory, "FluidEntityPlugin.java"));
        copyResourceIfMissing(getClass().getResource(FLUID_UTILITY_SOURCES_DIR + "/EBag.java"), new File(outputArtemisModuleDirectory, "EBag.java"));
        copyResourceIfMissing(getClass().getResource(FLUID_UTILITY_SOURCES_DIR + "/FluidIteratingSystem.java"), new File(outputArtemisModuleDirectory, "FluidIteratingSystem.java"));
    }

    private void copyResourceIfMissing(URL source, File destination) {
        try {
            if (!destination.exists()) {
                FileUtils.copyURLToFile(source, destination);
            }
        } catch (IOException e) {
            throw new RuntimeException("Fluid API generation aborted, could not copy FluidEntityPlugin.java to " + destination + ".\n", e);
        }
    }

    private Collection<Class<? extends Component>> filterComponents(Collection<Class<? extends Component>> unfilteredComponents, Log log) {
        final List<Class<? extends Component>> components = new ArrayList<Class<? extends Component>>();
        for (Class<? extends Component> component : unfilteredComponents) {

            if (Modifier.isAbstract(component.getModifiers()) || Modifier.isInterface(component.getModifiers())) {
                // Skip abstract components.
                log.info(".. Skipping abstract/interface: " + component.getName());
            } else if (component.equals(SerializationTag.class) || component.getName().startsWith("com.artemis.weaver.")) {
                // No reserved classes either.
                log.info(".. Skipping reserved class: " + component.getName());
            } else {
                // Include!
                components.add(component);
            }
        }
        return components;
    }

    /**
     * @param artemisModel
     * @param generator
     * @param file
     * @param log
     * @throws com.artemis.generator.validator.TypeModelValidatorException
     */
    private void generateFile(ArtemisModel artemisModel, TypeModelGenerator generator, File file, Log log) {
        try {
            FileWriter fileWriter = new FileWriter(file);
            try {
                TypeModel typeModel = createExampleTypeModel(generator, artemisModel);
                new TypeModelValidator(log, file.getName()).validate(typeModel);
                new PoetSourceGenerator().generate(typeModel, fileWriter);
            } finally {
                fileWriter.close();
            }

        } catch (TypeModelValidatorException e) {
            throw new RuntimeException("Fluid API generation aborted, duplicate components, component field or component method names might be to blame.\n", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ArtemisModel createArtemisModel(Collection<Class<? extends Component>> components, Collection<Class<? extends FieldProxyStrategy>> fieldProxyStrategiesClasses, FluidGeneratorPreferences globalPreferences, Log log) {
        return new ArtemisModel(
                createComponentDescriptors(components, globalPreferences, log),
                createFieldProxyStrategies(fieldProxyStrategiesClasses, log));
    }

    private ArrayList<ComponentDescriptor> createComponentDescriptors(Collection<Class<? extends Component>> components, FluidGeneratorPreferences globalPreferences, Log log) {
        ArrayList<ComponentDescriptor> results = new ArrayList<ComponentDescriptor>();
        for (Class<? extends Component> component : components) {
            ComponentDescriptor descriptor = ComponentDescriptor.create(component, globalPreferences);
            if (!descriptor.getPreferences().isExcludeFromGeneration()) {
                log.info(".. Including: " + component.getName());
                results.add(descriptor);
            } else {
                log.info(".. Excluded by annotation: " + component.getName());
            }
        }
        Collections.sort(results);
        return results;
    }

    private ArrayList<FieldProxyStrategy> createFieldProxyStrategies(Collection<Class<? extends FieldProxyStrategy>> fieldProxyStrategiesClasses, Log log) {
        ArrayList<FieldProxyStrategy> results = new ArrayList<>();
        for (Class<? extends FieldProxyStrategy> clazz : fieldProxyStrategiesClasses) {
            try {
                log.info(".. Registering field handler: " + clazz.getName());
                results.add(clazz.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("Failed to instance " + clazz.getName() + ".", e);
            }
        }
        Collections.sort(results, SORT_BY_PRIORITY_DESC_FALLBACK_ON_NAME);
        log.info("Default:" + (!results.isEmpty() ? results.get(0) : "missing"));
        return results;
    }

    private static TypeModel createExampleTypeModel(TypeModelGenerator generator, ArtemisModel artemisModel) {
        return generator.generate(artemisModel);
    }

    private static TypeModelGenerator createFluidInterfaceGenerator(FluidGeneratorPreferences preferences) {
        TypeModelGenerator generator = new TypeModelGenerator();
        generator.addStrategy(new EBaseStrategy());
        generator.addStrategy(new EQueryExtensionsStrategy());
        generator.addStrategy(new ComponentExistStrategy());
        generator.addStrategy(new ComponentCreateStrategy());
        if (preferences.isGenerateTagMethods()) {
            generator.addStrategy(new ComponentTagStrategy());
        }
        if (preferences.isGenerateGroupMethods()) {
            generator.addStrategy(new ComponentGroupStrategy());
        }
        generator.addStrategy(new ComponentRemoveStrategy());
        generator.addStrategy(new ComponentAccessorStrategy());
        generator.addStrategy(new ComponentFieldAccessorStrategy());
        generator.addStrategy(new ComponentMethodProxyStrategy());
        generator.addStrategy(new DeleteFromWorldStrategy());
        if (preferences.isGenerateBooleanComponentAccessors()) {
            generator.addStrategy(new FlagComponentBooleanAccessorStrategy());
        }
        return generator;
    }

    private static TypeModelGenerator createComponentsGenerator(FluidGeneratorPreferences preferences) {
        TypeModelGenerator generator = new TypeModelGenerator();
        generator.addStrategy(new ComponentsBaseStrategy());
        generator.addStrategy(new ComponentsClassLibraryStrategy());
        return generator;
    }

    private static TypeModelGenerator createSupermapperGenerator(FluidGeneratorPreferences preferences) {
        TypeModelGenerator generator = new TypeModelGenerator();
        generator.addStrategy(new SuperMapperStrategy());
        generator.addStrategy(new ComponentMapperFieldsStrategy());
        return generator;
    }
}
