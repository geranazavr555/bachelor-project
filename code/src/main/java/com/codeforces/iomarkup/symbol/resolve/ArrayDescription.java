package com.codeforces.iomarkup.symbol.resolve;

import lombok.Getter;

import java.util.List;

public abstract class ArrayDescription<T> implements VariableDescription {
    @Getter
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
