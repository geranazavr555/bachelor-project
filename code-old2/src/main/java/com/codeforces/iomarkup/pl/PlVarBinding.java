package com.codeforces.iomarkup.pl;

import com.codeforces.iomarkup.symbols.Variable;

public class PlVarBinding extends PlExpression {
    private final Variable variable;

    PlVarBinding(Variable variable) {
        this.variable = variable;
    }
}
