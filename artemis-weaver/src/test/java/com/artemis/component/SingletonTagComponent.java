package com.artemis.component;

import com.artemis.Component;
import com.artemis.annotations.PackedWeaver;

/**
 * {@link PackedWeaver} can be for weaving tag components too by
 * declaring the component without any fields.
 */
@PackedWeaver
public class SingletonTagComponent extends Component {}
