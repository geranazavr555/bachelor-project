package com.codeforces.iomarkup.pl;

import com.codeforces.iomarkup.types.Type;
import com.codeforces.iomarkup.types.primitive.Bool;
import com.codeforces.iomarkup.types.primitive.IntegerPrimitiveType;
import com.codeforces.iomarkup.types.primitive.NumericPrimitiveType;

public class PlUnaryOperator extends PlExpression {
    private final Op op;
    private final PlExpression argument;

    public PlUnaryOperator(Op op, PlExpression argument) {
        this.op = op;
        this.argument = argument;
        addBindedVars(argument);
    }

    @Override
    protected Type ensureTypeImpl() {
        Type argType = argument.ensureType();
        if (!Type.isAssignable(op.requiredArgumentTypeClass, argType))
            throw new RuntimeException(); // todo
        return argType;
    }

    public enum Op {
        LOGICAL_NOT(Bool.class),
        BITWISE_NOT(IntegerPrimitiveType.class),
        MINUS(NumericPrimitiveType.class);

        private final Class<? extends Type> requiredArgumentTypeClass;

        Op(Class<? extends Type> requiredArgumentTypeClass) {
            this.requiredArgumentTypeClass = requiredArgumentTypeClass;
        }
    }
}
