package com.artemis.generator.common;

import com.artemis.generator.model.ClassModel;

/**
 * Convert agnostic class model to java source.
 *
 * Created by Daan on 10-9-2016.
 */
public interface SourceGenerator {
    void generate(ClassModel model, Appendable out);
}
