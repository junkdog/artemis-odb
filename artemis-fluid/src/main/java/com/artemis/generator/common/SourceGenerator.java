package com.artemis.generator.common;

import com.artemis.generator.model.ClassModel;

import java.io.File;

/**
 * Created by Daan on 10-9-2016.
 */
public interface SourceGenerator {
    void generate(ClassModel model, Appendable out);
}
