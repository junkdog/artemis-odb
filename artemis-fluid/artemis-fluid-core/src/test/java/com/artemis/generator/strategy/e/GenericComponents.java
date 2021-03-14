package com.artemis.generator.strategy.e;

import com.artemis.Component;

abstract class SingleValueComponent<T> extends Component {
    public T value;
}

final class StringComponent extends SingleValueComponent<String> { }

abstract class BaseComponent<T1, T2> extends Component {
    public T1 value1;
    public T2 value2;

    public void set(T1 a, T2 b, int c) {
        value1 = a;
        value2 = b;
    }

    public T1 dummy(T1 a, T2 b, int c) {
        return value1;
    }
}

abstract class SubComponent<T> extends BaseComponent<T, String> { }

final class SimpleComponent extends SubComponent<Integer> { }
