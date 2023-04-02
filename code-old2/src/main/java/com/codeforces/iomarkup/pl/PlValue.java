package com.codeforces.iomarkup.pl;

import com.codeforces.iomarkup.types.Type;

public class PlValue<T> extends PlExpression {
    private final Type type;
    private final T value;

    public PlValue(Type type, T value) {
        this.type = type;
        this.value = value;
    }
}
