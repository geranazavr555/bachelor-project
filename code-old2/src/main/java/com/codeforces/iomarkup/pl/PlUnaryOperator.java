package com.codeforces.iomarkup.pl;

import com.codeforces.iomarkup.types.TypeCharacteristic;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import static com.codeforces.iomarkup.types.TypeCharacteristic.*;

public class PlUnaryOperator extends PlExpression {
    private final Op op;
    private final PlExpression argument;

    public PlUnaryOperator(Op op, PlExpression argument) {
        this.op = op;
        this.argument = argument;
    }

    public enum Op {
        LOGICAL_NOT(PREDEFINED, PRIMITIVE, BOOL),
        BITWISE_NOT(PREDEFINED, PRIMITIVE, INTEGER),
        MINUS(PREDEFINED, PRIMITIVE, NUMERIC);

        private final Set<TypeCharacteristic> requiredTypeCharacteristics;

        Op(TypeCharacteristic... requiredTypeCharacteristics) {
            this.requiredTypeCharacteristics =
                    Collections.unmodifiableSet(EnumSet.copyOf(Arrays.asList(requiredTypeCharacteristics)));
        }
    }
}
