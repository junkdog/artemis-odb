package com.artemis.generator.common;

import com.artemis.generator.model.artemis.ArtemisModel;
import com.artemis.generator.model.type.TypeModel;

/**
 * Strategy for generating builder model from component set.
 *
 * @author Daan van Yperen
 */
public interface BuilderModelStrategy {

    /** Apply strategy to model, generating whatever methods needed. */
    void apply(ArtemisModel artemisModel, TypeModel model);
}
