package com.codeforces.iomarkup.type;

import com.codeforces.iomarkup.pl.PlExpression;

import java.util.Collections;
import java.util.Set;

public class ArrayType implements Type {
    protected final Set<TypeCharacteristic> characteristics;
    private final PlExpression lengthExpression;
    private Type componentType;

    public ArrayType(Type componentType) {
        this(componentType, null);
    }

    public ArrayType(Type componentType, PlExpression lengthExpression) {
        this.componentType = componentType;
        this.lengthExpression = lengthExpression;

        Set<TypeCharacteristic> characteristics = TypeCharacteristic.modifiableClosure(TypeCharacteristic.ARRAY);

        if (TypeCharacteristic.PARAMETRIZED.is(componentType) || lengthExpression != null)
            characteristics.add(TypeCharacteristic.PARAMETRIZED);

        this.characteristics = characteristics;
    }

    @Override
    public Set<TypeCharacteristic> getCharacteristics() {
        return Collections.unmodifiableSet(characteristics);
    }
}
