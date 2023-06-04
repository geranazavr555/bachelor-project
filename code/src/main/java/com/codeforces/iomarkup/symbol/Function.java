package com.codeforces.iomarkup.symbol;

import com.codeforces.iomarkup.type.Type;
import com.codeforces.iomarkup.type.TypeCharacteristic;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class Function extends Symbol {
    private final Type returnType;
    private final List<Set<TypeCharacteristic>> requiredArgumentTypeCharacteristics;

    public Function(
            String name,
            Type returnType,
            List<Set<TypeCharacteristic>> requiredArgumentTypeCharacteristics) {
        super(name);
        this.returnType = returnType;
        this.requiredArgumentTypeCharacteristics = requiredArgumentTypeCharacteristics;
    }

    public Type returnType() {
        return returnType;
    }

    public List<Set<TypeCharacteristic>> requiredArgumentTypeCharacteristics() {
        return Collections.unmodifiableList(requiredArgumentTypeCharacteristics);
    }
}
