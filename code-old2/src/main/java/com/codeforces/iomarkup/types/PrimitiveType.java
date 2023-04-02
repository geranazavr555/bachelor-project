package com.codeforces.iomarkup.types;

import com.codeforces.iomarkup.pl.PlExpression;

import java.util.*;

import static com.codeforces.iomarkup.types.TypeCharacteristic.*;

public enum PrimitiveType implements Type {
    BOOL("bool", PREDEFINED, PRIMITIVE, LITERAL, TypeCharacteristic.BOOL, NAMED, CAN_EQUAL),
    CHAR("char", PREDEFINED, PRIMITIVE, LITERAL, TypeCharacteristic.CHAR, COMPARABLE, NAMED, CAN_EQUAL),
    INT32("int32", PREDEFINED, PRIMITIVE, LITERAL, INTEGER, COMPARABLE, NUMERIC, NAMED, CAN_EQUAL),
    UINT32("uint32", PREDEFINED, PRIMITIVE, LITERAL, INTEGER, COMPARABLE, NUMERIC, NAMED, CAN_EQUAL),
    INT64("int64", PREDEFINED, PRIMITIVE, LITERAL, INTEGER, COMPARABLE, NUMERIC, NAMED, CAN_EQUAL),
    UINT64("uint64", PREDEFINED, PRIMITIVE, LITERAL, INTEGER, COMPARABLE, NUMERIC, NAMED, CAN_EQUAL),
    FLOAT32("float32", PREDEFINED, PRIMITIVE, LITERAL, FLOAT, COMPARABLE, NUMERIC, NAMED, CAN_EQUAL),
    FLOAT64("float64", PREDEFINED, PRIMITIVE, LITERAL, FLOAT, COMPARABLE, NUMERIC, NAMED, CAN_EQUAL);

    private final String name;
    private final Set<TypeCharacteristic> characteristics;

    PrimitiveType(String name, TypeCharacteristic... characteristics) {
        this.name = name;
        this.characteristics = Collections.unmodifiableSet(EnumSet.copyOf(Arrays.asList(characteristics)));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Set<TypeCharacteristic> getCharacteristics() {
        return characteristics;
    }

    @Override
    public List<PlExpression> getParameterExpressions() {
        return List.of();
    }
}
