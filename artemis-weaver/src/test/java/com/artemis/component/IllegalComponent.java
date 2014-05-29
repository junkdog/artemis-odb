package com.artemis.component;

import com.artemis.Component;
import com.artemis.annotations.PackedWeaver;

@PackedWeaver
public class IllegalComponent extends Component {
	public Object ImNotAllowedHere;
}
