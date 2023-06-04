package com.codeforces.iomarkup.type;

import com.codeforces.iomarkup.pl.PlExpression;
import lombok.Getter;

import java.util.Collections;
import java.util.Set;

public class ArrayType implements Type {
    protected final Set<TypeCharacteristic> characteristics;

    @Getter
    private final PlExpression lengthExpression;

    @Getter
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

    @Override
    public boolean isAssignableFrom(Type other) {
        if (!TypeCharacteristic.isSubset(componentType.getCharacteristics(), other.getCharacteristics()) ||
            !TypeCharacteristic.isSubset(other.getCharacteristics(), componentType.getCharacteristics()))
            return false;

        if (other instanceof ArrayType arrayType) {
            return componentType.isAssignableFrom(arrayType.getComponentType()) &&
                   arrayType.getComponentType().isAssignableFrom(componentType);
        }

        return false;
    }
}
