package com.artemis.generator.common;

import com.artemis.generator.model.artemis.ArtemisModel;
import com.artemis.generator.model.type.TypeModel;

/**
 * Strategy for generating builder model from component set.
 *
 * Created by Daan on 10-9-2016.
 */
public interface BuilderModelStrategy {

    /** Apply strategy to model, generating whatever methods needed. */
    void apply(ArtemisModel artemisModel, TypeModel model);
}
