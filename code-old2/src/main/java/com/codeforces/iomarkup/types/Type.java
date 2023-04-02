package com.codeforces.iomarkup.types;

import com.codeforces.iomarkup.pl.PlExpression;

import java.util.List;
import java.util.Set;

public interface Type {
    String getName();
    Set<TypeCharacteristic> getCharacteristics();
    List<PlExpression> getParameterExpressions();
}
