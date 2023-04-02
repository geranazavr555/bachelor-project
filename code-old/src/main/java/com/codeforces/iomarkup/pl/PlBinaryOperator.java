package com.codeforces.iomarkup.pl;

import com.codeforces.iomarkup.types.traits.ComparableType;
import com.codeforces.iomarkup.types.Type;
import com.codeforces.iomarkup.types.primitive.*;

import java.util.Map;
import java.util.function.BiFunction;

public class PlBinaryOperator extends PlExpression {
    private final Op op;
    private final PlExpression left;
    private final PlExpression right;

    public PlBinaryOperator(Op op, PlExpression left, PlExpression right) {
        this.op = op;
        this.left = left;
        this.right = right;
        addBindedVars(left);
        addBindedVars(right);
    }

    @Override
    protected Type ensureTypeImpl() {
        Type leftType = left.ensureType();
        if (!Type.isAssignable(op.requiredArgsType, leftType))
            throw new RuntimeException(); // todo

        Type rightType = right.ensureType();
        if (!Type.isAssignable(op.requiredArgsType, rightType))
            throw new RuntimeException(); // todo

        return op.resultTypeFunction.apply(leftType, rightType);
    }

    private static final Map<IntegerPrimitiveType, Map<IntegerPrimitiveType, IntegerPrimitiveType>> integerMathCasts =
            Map.of(
                    Int32.getInstance(), Map.of(
                            Int64.getInstance(), Int64.getInstance(),
                            UInt32.getInstance(), Int64.getInstance(),
                            UInt64.getInstance(), UInt64.getInstance()
                    ),
                    Int64.getInstance(), Map.of(
                            UInt32.getInstance(), Int64.getInstance(),
                            UInt64.getInstance(), UInt64.getInstance()
                    ),
                    UInt32.getInstance(), Map.of(
                            UInt64.getInstance(), UInt64.getInstance()
                    ),
                    UInt64.getInstance(), Map.of()
            );

    private static Type commonType(Type type1, Type type2) {
        if (!Type.isAssignable(NumericPrimitiveType.class, type1) || !Type.isAssignable(NumericPrimitiveType.class, type2))
            throw new RuntimeException(); // todo

        if (type1.equals(type2))
            return type1;

        if (Type.isAssignable(IntegerPrimitiveType.class, type1) && Type.isAssignable(FloatPrimitiveType.class, type2))
            return type2;

        if (Type.isAssignable(IntegerPrimitiveType.class, type2) && Type.isAssignable(FloatPrimitiveType.class, type1))
            return type1;

        if (Float32.getInstance().isAssignableFrom(type1) && Float64.getInstance().isAssignableFrom(type2))
            return Float64.getInstance();

        if (Float32.getInstance().isAssignableFrom(type2) && Float64.getInstance().isAssignableFrom(type1))
            return Float64.getInstance();

        Type resultType = integerMathCasts.get(type1).get(type2);
        if (resultType == null)
            resultType = integerMathCasts.get(type2).get(type1);
        if (resultType == null)
            throw new RuntimeException(); // todo
        return resultType;
    }

    public enum Op {
        MULTIPLICATION(
                NumericPrimitiveType.class,
                PlBinaryOperator::commonType,
                Associativity.LEFT
        ),
        DIVISION(
                NumericPrimitiveType.class,
                PlBinaryOperator::commonType,
                Associativity.LEFT
        ),
        MODULO(
                IntegerPrimitiveType.class,
                PlBinaryOperator::commonType,
                Associativity.LEFT
        ),
        ADDITION(
                NumericPrimitiveType.class,
                PlBinaryOperator::commonType,
                Associativity.LEFT
        ),
        SUBTRACTION(
                NumericPrimitiveType.class,
                PlBinaryOperator::commonType,
                Associativity.LEFT
        ),
        GREATER(
                ComparableType.class,
                (type1, type2) -> Bool.getInstance(),
                Associativity.LEFT
        ),
        GREATER_OR_EQUAL(
                ComparableType.class,
                (type1, type2) -> Bool.getInstance(),
                Associativity.LEFT
        ),
        LESS(
                ComparableType.class,
                (type1, type2) -> Bool.getInstance(),
                Associativity.LEFT
        ),
        LESS_OR_EQUAL(
                ComparableType.class,
                (type1, type2) -> Bool.getInstance(),
                Associativity.LEFT
        ),
        EQUAL(
                ComparableType.class,
                (type1, type2) -> Bool.getInstance(),
                Associativity.LEFT
        ),
        NOT_EQUAL(
                ComparableType.class,
                (type1, type2) -> Bool.getInstance(),
                Associativity.LEFT
        ),
        BITWISE_AND(
                IntegerPrimitiveType.class,
                PlBinaryOperator::commonType,
                Associativity.LEFT
        ),
        BITWISE_XOR(
                IntegerPrimitiveType.class,
                PlBinaryOperator::commonType,
                Associativity.LEFT
        ),
        BITWISE_OR(
                IntegerPrimitiveType.class,
                PlBinaryOperator::commonType,
                Associativity.LEFT
        ),
        LOGICAL_AND(
                Bool.class,
                (type1, type2) -> Bool.getInstance(),
                Associativity.LEFT
        ),
        LOGICAL_OR(
                Bool.class,
                (type1, type2) -> Bool.getInstance(),
                Associativity.LEFT
        );

        private final Class<? extends Type> requiredArgsType;
        private final BiFunction<Type, Type, Type> resultTypeFunction;
        private final Associativity associativity;

        Op(Class<? extends Type> requiredArgsType, BiFunction<Type, Type, Type> resultTypeFunction, Associativity associativity) {
            this.requiredArgsType = requiredArgsType;
            this.resultTypeFunction = resultTypeFunction;
            this.associativity = associativity;
        }
    }

    public enum Associativity {
        LEFT, RIGHT
    }
}
