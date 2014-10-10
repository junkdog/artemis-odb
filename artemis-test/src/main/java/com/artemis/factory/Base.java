package com.artemis.factory;

import com.artemis.EntityFactory;

//public interface Base <T extends EntityFactory<T>> extends EntityFactory<T> {
public interface Base extends EntityFactory<Base> {
	Base base1();
	Base base2();
	Base base3();
}
