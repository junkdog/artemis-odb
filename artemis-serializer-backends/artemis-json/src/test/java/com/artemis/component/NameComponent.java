package com.artemis.component;

import com.artemis.Component;

public class NameComponent extends Component {
	public String name;

	public NameComponent(String name) {
		this.name = name;
	}

	public NameComponent() {
		this("");
	}
}