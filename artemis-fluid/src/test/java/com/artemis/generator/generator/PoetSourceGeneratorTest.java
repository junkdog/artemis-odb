package com.artemis.generator.generator;

import com.artemis.generator.common.SourceGenerator;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Daan on 10-9-2016.
 */
public class PoetSourceGeneratorTest extends SourceGeneratorTest {

    @Override
    protected SourceGenerator getSourceGenerator() {
        return new PoetSourceGenerator();
    }
}