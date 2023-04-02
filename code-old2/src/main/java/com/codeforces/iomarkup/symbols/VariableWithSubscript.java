package com.codeforces.iomarkup.symbols;

import com.codeforces.iomarkup.pl.PlExpression;

import javax.annotation.Nonnull;

public record VariableWithSubscript(String name, @Nonnull PlExpression subscriptExpression)
        implements VariablePathElement {
}
