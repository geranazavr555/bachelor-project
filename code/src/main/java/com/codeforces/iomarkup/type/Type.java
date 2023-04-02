package com.codeforces.iomarkup.type;

import com.codeforces.iomarkup.symbol.resolve.VariableDescription;

import java.util.Set;

public interface Type extends VariableDescription {
    Set<TypeCharacteristic> getCharacteristics();

    default String getName() {
        return null;
    }
}
