package com.artemis.generator.generator;

import com.artemis.generator.common.SourceGenerator;

/**
 * Created by Daan on 10-9-2016.
 */
public class PoetSourceGeneratorTest extends AbstractSourceGeneratorTest {

    @Override
    protected SourceGenerator getSourceGenerator() {
        return new PoetSourceGenerator();
    }
}