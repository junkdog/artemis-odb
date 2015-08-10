package com.artemis.manager;

import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.artemis.factory.Ship;
import com.artemis.factory.ShipNoMethods;
import com.artemis.factory.ShipShortWire;

@Wire
public class TypedEntityFactory extends Manager {
	public Ship ship;
	public ShipNoMethods shipNoMethods;
	public ShipShortWire shipShortWire;
}