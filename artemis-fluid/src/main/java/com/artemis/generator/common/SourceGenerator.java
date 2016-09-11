package com.artemis.generator.common;

import com.artemis.generator.model.type.TypeModel;

/**
 * Convert agnostic class model to java source.
 *
 * @author Daan van Yperen
 */
public interface SourceGenerator {
    void generate(TypeModel model, Appendable out);
}
