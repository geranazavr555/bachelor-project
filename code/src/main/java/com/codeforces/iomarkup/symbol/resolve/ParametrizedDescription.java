package com.codeforces.iomarkup.symbol.resolve;

import com.codeforces.iomarkup.pl.PlExpression;
import com.codeforces.iomarkup.type.Type;

import java.util.Collections;
import java.util.List;

public record ParametrizedDescription(Type type, List<PlExpression> arguments) implements VariableDescription {
    @Override
    public List<PlExpression> arguments() {
        return Collections.unmodifiableList(arguments);
    }
}
