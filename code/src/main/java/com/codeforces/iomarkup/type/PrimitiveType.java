package com.codeforces.iomarkup.type;

import java.util.Set;

public enum PrimitiveType implements Type {
    BOOL("bool", TypeCharacteristic.BOOL),
    CHAR("char", TypeCharacteristic.CHAR),
    INT32("int32", TypeCharacteristic.INTEGER),
    UINT32("uint32", TypeCharacteristic.INTEGER),
    INT64("int64", TypeCharacteristic.INTEGER),
    UINT64("uint64", TypeCharacteristic.INTEGER),
    FLOAT32("float32", TypeCharacteristic.FLOAT),
    FLOAT64("float64", TypeCharacteristic.FLOAT);

    private final String name;
    private final Set<TypeCharacteristic> characteristics;

    PrimitiveType(String name, TypeCharacteristic baseCharacteristic) {
        this.name = name;
        this.characteristics = baseCharacteristic.closure();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Set<TypeCharacteristic> getCharacteristics() {
        return characteristics;
    }
}
