package com.artemis;

import com.artemis.generator.TypeModelGenerator;
import com.artemis.generator.generator.PoetSourceGenerator;
import com.artemis.generator.model.artemis.ArtemisModel;
import com.artemis.generator.model.artemis.ComponentDescriptor;
import com.artemis.generator.model.type.TypeModel;
import com.artemis.generator.strategy.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Daan van Yperen
 */
public class FluidGenerator {

    public void generate(Collection<Class<? extends Component>> components, File outputDirectory ) {

        ArtemisModel artemisModel = createArtemisModel(components);

        new File(outputDirectory, "com/artemis/").mkdirs();

        generateFile(artemisModel, createEGenerator(), new File(outputDirectory, "com/artemis/E.java"));
        generateFile(artemisModel, createSupermapperGenerator(), new File(outputDirectory, "com/artemis/SuperMapper.java"));
    }

    private void generateFile(ArtemisModel artemisModel, TypeModelGenerator generator, File file) {
        try {
            FileWriter fileWriter = new FileWriter(file);
            try {
                TypeModel typeModel = createExampleTypeModel(generator, artemisModel);
                new PoetSourceGenerator().generate(typeModel, fileWriter);
            } finally {
                fileWriter.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
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
        generator.addStrategy(new CreateLifecycleStrategy());
        generator.addStrategy(new DirectAccessorStrategy());
        return generator;
    }

    private static TypeModelGenerator createSupermapperGenerator() {
        TypeModelGenerator generator = new TypeModelGenerator();
        generator.addStrategy(new SuperMapperStrategy());
        generator.addStrategy(new ComponentMapperFieldsStrategy());
        return generator;
    }

}
