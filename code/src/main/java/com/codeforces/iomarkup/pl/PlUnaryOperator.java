package com.codeforces.iomarkup.pl;

import com.codeforces.iomarkup.type.Type;
import com.codeforces.iomarkup.type.TypeCharacteristic;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

@AllArgsConstructor
@Getter
public class PlUnaryOperator extends PlExpression {
    private final Op op;
    private final PlExpression expression;

    @Override
    public Type getType() {
        var type = expression.getType();
        for (TypeCharacteristic characteristic : op.requiredTypeCharacteristics) {
            if (!characteristic.is(type))
                throw new RuntimeException();
        }
        return type;
    }

    public enum Op {
        LOGICAL_NOT(TypeCharacteristic.BOOL),
        BITWISE_NOT(TypeCharacteristic.INTEGER),
        MINUS(TypeCharacteristic.NUMERIC);

        private final Set<TypeCharacteristic> requiredTypeCharacteristics;

        Op(TypeCharacteristic... characteristics) {
            requiredTypeCharacteristics = Collections.unmodifiableSet(EnumSet.of(characteristics[0], characteristics));
        }
    }
}
