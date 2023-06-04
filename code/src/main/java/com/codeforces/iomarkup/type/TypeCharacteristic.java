package com.codeforces.iomarkup.type;

import java.util.*;

public enum TypeCharacteristic {
    PREDEFINED,
    COMPARABLE,
    CAN_EQUAL,
    LITERAL,
    NAMED,

    PRIMITIVE(LITERAL, PREDEFINED, NAMED),

    NUMERIC(CAN_EQUAL, COMPARABLE, PRIMITIVE),
    FLOAT(NUMERIC),
    INTEGER(NUMERIC),

    BOOL(PRIMITIVE, CAN_EQUAL),
    CHAR(PRIMITIVE, CAN_EQUAL, COMPARABLE),

    STRUCT,
    ARRAY,
    PARAMETRIZED;

    private final Set<TypeCharacteristic> implies;

    TypeCharacteristic(TypeCharacteristic... implies) {
        this.implies = Set.of(implies);
    }

    public Set<TypeCharacteristic> closure() {
        Set<TypeCharacteristic> closure = EnumSet.of(this);
        boolean changed;
        do {
            changed = false;
            for (TypeCharacteristic typeCharacteristic : closure)
                changed |= closure.addAll(typeCharacteristic.implies);
        } while (changed);
        return Collections.unmodifiableSet(closure);
    }

    public boolean is(Type type) {
        return type.getCharacteristics().contains(this);
    }

    public static Set<TypeCharacteristic> modifiableClosure(TypeCharacteristic... characteristics) {
        Set<TypeCharacteristic> closure = EnumSet.noneOf(TypeCharacteristic.class);
        for (TypeCharacteristic characteristic : characteristics) {
            if (characteristic != null)
                closure.addAll(characteristic.closure());
        }
        return closure;
    }

    public static Set<TypeCharacteristic> closure(TypeCharacteristic... characteristics) {
        return Collections.unmodifiableSet(modifiableClosure(characteristics));
    }

    public static boolean isSubset(Set<TypeCharacteristic> set, Set<TypeCharacteristic> subset) {
        return set.containsAll(subset);
    }
}
