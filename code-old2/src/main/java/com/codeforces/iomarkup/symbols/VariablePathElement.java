package com.codeforces.iomarkup.symbols;

import com.codeforces.iomarkup.pl.PlExpression;

import javax.annotation.Nullable;

public interface VariablePathElement {
    @Nullable
    default PlExpression subscriptExpression() {
        return null;
    }
}
