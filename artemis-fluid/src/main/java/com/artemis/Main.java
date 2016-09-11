package com.artemis;

import com.artemis.generator.TypeModelGenerator;
import com.artemis.generator.generator.PoetSourceGenerator;
import com.artemis.generator.model.artemis.ArtemisModel;
import com.artemis.generator.model.artemis.ComponentDescriptor;
import com.artemis.generator.model.type.TypeModel;
import com.artemis.generator.strategy.*;

import java.util.ArrayList;

/**
 * @author Daan van Yperen
 */
public class Main {

    public static class Pos extends Component {};
    public static class Physics extends Component {};
    public static class Anim extends Component {};

    public static void main( String[] args ) {

        TypeModelGenerator generatorW = createFluidWGenerator();
        TypeModelGenerator generatorE = createFluidEGenerator();

        System.out.println("```java");

        TypeModel typeModel = createExampleTypeModel(generatorE);
        new PoetSourceGenerator().generate(typeModel, System.out);

        System.out.println("```");
        System.out.println("```java");

        typeModel = createExampleTypeModel(generatorW);
        new PoetSourceGenerator().generate(typeModel, System.out);

        System.out.println("```");
    }

    private static TypeModel createExampleTypeModel(TypeModelGenerator generator) {
        ArrayList<ComponentDescriptor> components = new ArrayList<ComponentDescriptor>();
        components.add(new ComponentDescriptor(Pos.class));
        components.add(new ComponentDescriptor(Physics.class));
        components.add(new ComponentDescriptor(Anim.class));
        return generator.generate(new ArtemisModel(components));
    }

    private static TypeModelGenerator createFluidEGenerator() {
        TypeModelGenerator generator = new TypeModelGenerator();
        generator.addStrategy(new EBaseStrategy());
        generator.addStrategy(new CreateLifecycleStrategy());
        generator.addStrategy(new DirectAccessorStrategy());
        return generator;
    }

    private static TypeModelGenerator createFluidWGenerator() {
        TypeModelGenerator generator = new TypeModelGenerator();
        generator.addStrategy(new SuperMapperStrategy());
        generator.addStrategy(new ComponentMapperFieldsStrategy());
        return generator;
    }
}
