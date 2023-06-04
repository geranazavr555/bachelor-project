package com.codeforces.iomarkup.pl;

import com.codeforces.iomarkup.symbol.Function;
import com.codeforces.iomarkup.type.Type;
import com.codeforces.iomarkup.type.TypeCharacteristic;
import lombok.AllArgsConstructor;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class PlFunctionCall extends PlExpression {
    private final Function function;
    private final List<PlExpression> argExpressions;

    public Function getFunction() {
        return function;
    }

    public List<PlExpression> getArgExpressions() {
        return Collections.unmodifiableList(argExpressions);
    }

    @Override
    public Type getType() {
        var expected = function.requiredArgumentTypeCharacteristics();
        if (argExpressions.size() != expected.size())
            throw new RuntimeException();

        for (int i = 0; i < argExpressions.size(); i++) {
            var providedCharacteristics = argExpressions.get(i).getType().getCharacteristics();
            var requiredCharacteristics = expected.get(i);
            if (!TypeCharacteristic.isSubset(providedCharacteristics, requiredCharacteristics))
                throw new RuntimeException();
        }

        return function.returnType();
    }
}
