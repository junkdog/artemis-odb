package com.artemis.generator.generator;

import com.artemis.generator.common.SourceGenerator;

/**
 * @author Daan van Yperen
 */
public class PoetSourceGeneratorTest extends AbstractSourceGeneratorTest {

    @Override
    protected SourceGenerator getSourceGenerator() {
        return new PoetSourceGenerator();
    }
}