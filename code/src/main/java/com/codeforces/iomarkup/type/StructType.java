package com.codeforces.iomarkup.type;

import java.util.Objects;
import java.util.Set;

public class StructType implements Type {
    private final String name;
    private final Set<TypeCharacteristic> characteristics;

    public StructType() {
        this.name = null;
        this.characteristics = TypeCharacteristic.closure(TypeCharacteristic.STRUCT);
    }

    public StructType(String name, boolean parametrized) {
        this.name = name;
        this.characteristics = TypeCharacteristic.closure(
                TypeCharacteristic.NAMED,
                TypeCharacteristic.STRUCT,
                (parametrized ? TypeCharacteristic.PARAMETRIZED : null)
        );
    }

    @Override
    public Set<TypeCharacteristic> getCharacteristics() {
        return characteristics;
    }

    @Override
    public boolean isAssignableFrom(Type other) {
        if (!TypeCharacteristic.NAMED.is(this) || !TypeCharacteristic.NAMED.is(other))
            return false;

        return Objects.equals(getName(), other.getName());
    }

    @Override
    public String getName() {
        return name;
    }
}
