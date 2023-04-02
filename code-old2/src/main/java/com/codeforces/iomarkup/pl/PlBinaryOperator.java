package com.codeforces.iomarkup.pl;

import com.codeforces.iomarkup.types.PrimitiveType;
import com.codeforces.iomarkup.types.Type;
import com.codeforces.iomarkup.types.TypeCharacteristic;

import java.util.*;
import java.util.function.BiFunction;

import static com.codeforces.iomarkup.types.TypeCharacteristic.*;

public class PlBinaryOperator extends PlExpression {
    private final Op op;
    private final PlExpression left;
    private final PlExpression right;

    public PlBinaryOperator(Op op, PlExpression left, PlExpression right) {
        this.op = op;
        this.left = left;
        this.right = right;
    }

    private static final List<Type> typeOrder = List.of(
            PrimitiveType.INT32,
            PrimitiveType.UINT32,
            PrimitiveType.INT64,
            PrimitiveType.UINT64
    );

    public static Type numericCommonType(Type type1, Type type2) {
        return (typeOrder.indexOf(type1) < typeOrder.indexOf(type2) ? type2 : type1);
    }

    public enum Op {
        MULTIPLICATION(
                Associativity.LEFT,
                PlBinaryOperator::numericCommonType,
                NUMERIC
        ),
        DIVISION(
                Associativity.LEFT,
                PlBinaryOperator::numericCommonType,
                NUMERIC
        ),
        MODULO(
                Associativity.LEFT,
                PlBinaryOperator::numericCommonType,
                INTEGER
        ),
        ADDITION(
                Associativity.LEFT,
                PlBinaryOperator::numericCommonType,
                NUMERIC
        ),
        SUBTRACTION(
                Associativity.LEFT,
                PlBinaryOperator::numericCommonType,
                NUMERIC
        ),
        GREATER(
                Associativity.LEFT,
                (x, y) -> PrimitiveType.BOOL,
                COMPARABLE
        ),
        GREATER_OR_EQUAL(
                Associativity.LEFT,
                (x, y) -> PrimitiveType.BOOL,
                COMPARABLE
        ),
        LESS(
                Associativity.LEFT,
                (x, y) -> PrimitiveType.BOOL,
                COMPARABLE
        ),
        LESS_OR_EQUAL(
                Associativity.LEFT,
                (x, y) -> PrimitiveType.BOOL,
                COMPARABLE
        ),
        EQUAL(
                Associativity.LEFT,
                (x, y) -> PrimitiveType.BOOL,
                CAN_EQUAL
        ),
        NOT_EQUAL(
                Associativity.LEFT,
                (x, y) -> PrimitiveType.BOOL,
                CAN_EQUAL
        ),
        BITWISE_AND(
                Associativity.LEFT,
                PlBinaryOperator::numericCommonType,
                INTEGER
        ),
        BITWISE_XOR(
                Associativity.LEFT,
                PlBinaryOperator::numericCommonType,
                INTEGER
        ),
        BITWISE_OR(
                Associativity.LEFT,
                PlBinaryOperator::numericCommonType,
                INTEGER
        ),
        LOGICAL_AND(
                Associativity.LEFT,
                (x, y) -> PrimitiveType.BOOL,
                BOOL
        ),
        LOGICAL_OR(
                Associativity.LEFT,
                (x, y) -> PrimitiveType.BOOL,
                BOOL
        );

        private final Associativity associativity;
        private final BiFunction<Type, Type, Type> commonTypeFunction;
        private final Set<TypeCharacteristic> requiredTypeCharacteristics;

        Op(Associativity associativity, BiFunction<Type, Type, Type> commonTypeFunction, TypeCharacteristic... requiredTypeCharacteristics) {
            this.associativity = associativity;
            this.commonTypeFunction = commonTypeFunction;
            this.requiredTypeCharacteristics =
                    Collections.unmodifiableSet(EnumSet.copyOf(Arrays.asList(requiredTypeCharacteristics)));
        }
    }

    public enum Associativity {
        LEFT, RIGHT
    }
}
