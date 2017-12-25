package com.artemis;

import com.artemis.components.SerializationTag;
import com.artemis.generator.TypeModelGenerator;
import com.artemis.generator.collect.AbstractComponentCollectStrategy;
import com.artemis.generator.collect.ReflectionsComponentCollectStrategy;
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
        generate(collectStrategy().allComponents(urls), outputDirectory, log, globalPreferences);
    }


    private AbstractComponentCollectStrategy collectStrategy() {
        return new ReflectionsComponentCollectStrategy();
    }

    /**
     * Generate fluid API files.
     *
     * @param components        components to consider.
     * @param outputDirectory   source root.
     * @param log               output.
     * @param globalPreferences
     * @throws com.artemis.generator.validator.TypeModelValidatorException
     */
    public void generate(Collection<Class<? extends Component>> components, File outputDirectory, Log log, FluidGeneratorPreferences globalPreferences) {

        ArtemisModel artemisModel = createArtemisModel(filterComponents(components, log), globalPreferences, log);

        File outputArtemisModuleDirectory = new File(outputDirectory, COM_ARTEMIS_MODULE_DIR);
        outputArtemisModuleDirectory.mkdirs();

        generateFile(artemisModel, createSupermapperGenerator(globalPreferences), new File(outputArtemisModuleDirectory, "SuperMapper.java"), log);
        generateFile(artemisModel, createEGenerator(globalPreferences), new File(outputArtemisModuleDirectory, "E.java"), log);
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

    private ArtemisModel createArtemisModel(Collection<Class<? extends Component>> components, FluidGeneratorPreferences globalPreferences, Log log) {
        ArrayList<ComponentDescriptor> componentDescriptors = new ArrayList<ComponentDescriptor>();
        for (Class<? extends Component> component : components) {
            ComponentDescriptor descriptor = ComponentDescriptor.create(component, globalPreferences);
            if (!descriptor.getPreferences().isExcludeFromGeneration()) {
                log.info(".. Including: " + component.getName());
                componentDescriptors.add(descriptor);
            } else {
                log.info(".. Excluded by annotation: " + component.getName());
            }
        }
        Collections.sort(componentDescriptors);
        return new ArtemisModel(componentDescriptors);
    }

    private static TypeModel createExampleTypeModel(TypeModelGenerator generator, ArtemisModel artemisModel) {
        return generator.generate(artemisModel);
    }

    private static TypeModelGenerator createEGenerator(FluidGeneratorPreferences preferences) {
        TypeModelGenerator generator = new TypeModelGenerator();
        generator.addStrategy(new EBaseStrategy());
        generator.addStrategy(new ComponentExistStrategy());
        generator.addStrategy(new ComponentCreateStrategy());
        if (preferences.isGenerateTagMethods()) generator.addStrategy(new ComponentTagStrategy());
        if (preferences.isGenerateGroupMethods()) generator.addStrategy(new ComponentGroupStrategy());
        generator.addStrategy(new ComponentRemoveStrategy());
        generator.addStrategy(new ComponentAccessorStrategy());
        generator.addStrategy(new ComponentFieldAccessorStrategy());
        generator.addStrategy(new DeleteFromWorldStrategy());
        if (preferences.isGenerateBooleanComponentAccessors())
            generator.addStrategy(new FlagComponentBooleanAccessorStrategy());
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
