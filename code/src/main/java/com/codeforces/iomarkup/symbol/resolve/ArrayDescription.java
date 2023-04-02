package com.codeforces.iomarkup.symbol.resolve;

import java.util.List;

public abstract class ArrayDescription<T> implements VariableDescription {
    private final List<ArrayParameters> arrayParameters;
    private final T component;

    protected ArrayDescription(List<ArrayParameters> arrayParameters, T component) {
        this.arrayParameters = arrayParameters;
        this.component = component;
    }

    public T getComponent() {
        return component;
    }
}
