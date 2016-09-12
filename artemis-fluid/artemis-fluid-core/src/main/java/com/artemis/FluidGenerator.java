package com.artemis;

import com.artemis.components.SerializationTag;
import com.artemis.generator.TypeModelGenerator;
import com.artemis.generator.collect.ReflectionComponentCollectStrategy;
import com.artemis.generator.generator.PoetSourceGenerator;
import com.artemis.generator.model.artemis.ArtemisModel;
import com.artemis.generator.model.artemis.ComponentDescriptor;
import com.artemis.generator.model.type.TypeModel;
import com.artemis.generator.strategy.e.*;
import com.artemis.generator.strategy.supermapper.ComponentMapperFieldsStrategy;
import com.artemis.generator.strategy.supermapper.SuperMapperStrategy;
import com.artemis.generator.util.Log;
import com.artemis.generator.validator.TypeModelValidator;
import com.artemis.generator.validator.TypeModelValidatorException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Fluid api class generator.
 *
 * @author Daan van Yperen
 */
public class FluidGenerator {

    /**
     * Generate fluid API files.
     * Finds all Component instances known to classloader using reflection.
     *
     * @param loader classloader to reflect over.
     * @param outputDirectory source root.
     * @param log output.
     * @throws com.artemis.generator.validator.TypeModelValidatorException
     */
    public void generate(ClassLoader loader, File outputDirectory, Log log ) {
        generate(new ReflectionComponentCollectStrategy().allComponents(loader), outputDirectory, log);
    }

    /**
     * Generate fluid API files.
     * Finds all Component instances at given urls using reflection.
     *
     * @param urls classpath urls to reflect over.
     * @param outputDirectory source root.
     * @param log output.
     * @throws com.artemis.generator.validator.TypeModelValidatorException
     */
    public void generate(Set<URL> urls, File outputDirectory, Log log ) {
        generate(new ReflectionComponentCollectStrategy().allComponents(urls), outputDirectory, log);
    }

    /**
     * Generate fluid API files.
     *
     * @param components components to consider.
     * @param outputDirectory source root.
     * @param log output.
     * @throws com.artemis.generator.validator.TypeModelValidatorException
     */
    public void generate(Collection<Class<? extends Component>> components, File outputDirectory, Log log ) {

        ArtemisModel artemisModel = createArtemisModel(filterComponents(components, log));

        new File(outputDirectory, "com/artemis/").mkdirs();

        generateFile(artemisModel, createSupermapperGenerator(), new File(outputDirectory, "com/artemis/SuperMapper.java"), log);
        generateFile(artemisModel, createEGenerator(), new File(outputDirectory, "com/artemis/E.java"), log);
    }

    private Collection<Class<? extends Component>> filterComponents(Collection<Class<? extends Component>> unfilteredComponents, Log log) {
            final List<Class<? extends Component>> components = new ArrayList<Class<? extends Component>>();
            for (Class<? extends Component> component : unfilteredComponents) {

                if (Modifier.isAbstract(component.getModifiers())) {
                    // Skip abstract components.
                    log.info(".. Skipping abstract: " + component.toString());
                } else if (component.equals(SerializationTag.class) || component.getName().startsWith("com.artemis.weaver.")) {
                    // No reserved classes either.
                    log.info(".. Skipping reserved class: " + component.toString());
                } else {
                    // Include!
                    log.info(".. Including: " + component.toString());
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
                new TypeModelValidator(log,file.getName()).validate(typeModel);
                new PoetSourceGenerator().generate(typeModel, fileWriter);
            } finally {
                fileWriter.close();
            }

        } catch(TypeModelValidatorException e) {
            throw new RuntimeException("Fluid API generation aborted, duplicate components, component field or component method names might be to blame.\n", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ArtemisModel createArtemisModel(Collection<Class<? extends Component>> components) {
        ArrayList<ComponentDescriptor> componentDescriptors = new ArrayList<ComponentDescriptor>();
        for (Class<? extends Component> component : components) {
            componentDescriptors.add(new ComponentDescriptor(component));
        }
        return new ArtemisModel(componentDescriptors);
    }

    private static TypeModel createExampleTypeModel(TypeModelGenerator generator, ArtemisModel artemisModel) {
        ArrayList<ComponentDescriptor> components = new ArrayList<ComponentDescriptor>();
        return generator.generate(artemisModel);
    }

    private static TypeModelGenerator createEGenerator() {
        TypeModelGenerator generator = new TypeModelGenerator();
        generator.addStrategy(new EBaseStrategy());
        generator.addStrategy(new ComponentCreateStrategy());
        generator.addStrategy(new ComponentAccessorStrategy());
        generator.addStrategy(new ComponentFieldAccessorStrategy());
        generator.addStrategy(new FieldComponentBooleanAccessorStrategy());
        return generator;
    }

    private static TypeModelGenerator createSupermapperGenerator() {
        TypeModelGenerator generator = new TypeModelGenerator();
        generator.addStrategy(new SuperMapperStrategy());
        generator.addStrategy(new ComponentMapperFieldsStrategy());
        return generator;
    }

}