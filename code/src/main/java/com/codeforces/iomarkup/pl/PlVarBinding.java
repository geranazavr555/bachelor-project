package com.codeforces.iomarkup.pl;

import com.codeforces.iomarkup.symbols.Variable;
import com.codeforces.iomarkup.types.Type;

import java.util.Optional;

public class PlVarBinding extends PlExpression {
    private final Variable variable;

    PlVarBinding(Variable variable) {
        super(variable.getType().orElse(null));
        this.variable = variable;
        addBindedVar(variable);
    }

    @Override
    public Type ensureTypeImpl() {
        Optional<Type> type = variable.getType();
        if (type.isEmpty())
            throw new RuntimeException(); // todo
        return type.get();
    }
}
