package com.codeforces.iomarkup.type;

import com.codeforces.iomarkup.symbol.Symbol;

public final class NamedType extends Symbol {
    private final Type type;

    public NamedType(Type type) {
        super(type.getName());
        assert TypeCharacteristic.NAMED.is(type);
        this.type = type;
    }

    public Type type() {
        return type;
    }

    @Override
    public String toString() {
        return "NamedType{" +
                "name=" + getName() + ", " +
                "type=" + type +
                "}";
    }
}