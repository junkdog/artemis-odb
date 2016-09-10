package com.artemis.generator.common;

import com.artemis.generator.model.type.TypeModel;

/**
 * Convert agnostic class model to java source.
 *
 * Created by Daan on 10-9-2016.
 */
public interface SourceGenerator {
    void generate(TypeModel model, Appendable out);
}
