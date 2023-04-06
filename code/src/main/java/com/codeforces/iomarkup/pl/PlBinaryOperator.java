package com.codeforces.iomarkup.pl;

import com.codeforces.iomarkup.type.TypeCharacteristic;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Getter
public class PlBinaryOperator extends PlExpression {
    private final Op op;
    private final List<PlExpression> expressions;

    public PlBinaryOperator(Op op, PlExpression left, PlExpression right) {
        this.op = op;
        this.expressions = List.of(left, right);
    }

    public enum Op {
        MULTIPLICATION(Associativity.LEFT, TypeCharacteristic.NUMERIC),
        DIVISION(Associativity.LEFT, TypeCharacteristic.NUMERIC),
        MODULO(Associativity.LEFT, TypeCharacteristic.INTEGER),
        ADDITION(Associativity.LEFT, TypeCharacteristic.NUMERIC),
        SUBTRACTION(Associativity.LEFT, TypeCharacteristic.NUMERIC),
        GREATER(null, TypeCharacteristic.COMPARABLE),
        GREATER_OR_EQUAL(null, TypeCharacteristic.COMPARABLE),
        LESS(null, TypeCharacteristic.COMPARABLE),
        LESS_OR_EQUAL(null, TypeCharacteristic.COMPARABLE),
        EQUAL(Associativity.LEFT, TypeCharacteristic.CAN_EQUAL),
        NOT_EQUAL(Associativity.LEFT, TypeCharacteristic.CAN_EQUAL),
        BITWISE_AND(Associativity.LEFT, TypeCharacteristic.INTEGER),
        BITWISE_XOR(Associativity.LEFT, TypeCharacteristic.INTEGER),
        BITWISE_OR(Associativity.LEFT, TypeCharacteristic.INTEGER),
        LOGICAL_AND(Associativity.LEFT, TypeCharacteristic.BOOL),
        LOGICAL_OR(Associativity.LEFT, TypeCharacteristic.BOOL),
        POW(Associativity.RIGHT, TypeCharacteristic.NUMERIC),
        BITWISE_SHIFT_LEFT(Associativity.LEFT, TypeCharacteristic.INTEGER),
        BITWISE_SHIFT_RIGHT(Associativity.LEFT, TypeCharacteristic.INTEGER);

        @Getter
        private final Associativity associativity;
        private final Set<TypeCharacteristic> requiredTypeCharacteristics;

        Op(Associativity associativity, TypeCharacteristic... characteristics) {
            this.associativity = associativity;
            this.requiredTypeCharacteristics = Collections.unmodifiableSet(EnumSet.of(characteristics[0], characteristics));
        }
    }

    public enum Associativity {
        LEFT, RIGHT
    }
}
