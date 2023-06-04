package com.codeforces.iomarkup.pl;

import com.codeforces.iomarkup.type.PrimitiveType;
import com.codeforces.iomarkup.type.Type;
import com.codeforces.iomarkup.type.TypeCharacteristic;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;

@AllArgsConstructor
@Getter
public class PlBinaryOperator extends PlExpression {
    private final Op op;
    private final List<PlExpression> expressions;

    public PlBinaryOperator(Op op, PlExpression left, PlExpression right) {
        this.op = op;
        this.expressions = List.of(left, right);
    }

    @Override
    public Type getType() {
        for (PlExpression expression : expressions) {
            var type = expression.getType();
            for (TypeCharacteristic characteristic : op.requiredTypeCharacteristics) {
                if (!characteristic.is(type)) {
                    throw new RuntimeException();
                }
            }
        }

        if (op.returnType != null)
            return op.returnType;

        List<PrimitiveType> primitiveTypes = new ArrayList<>(expressions.size());
        for (PlExpression expression : expressions) {
            var type = expression.getType();
            if (!(type instanceof PrimitiveType primitiveType))
                return expressions.get(0).getType();

            primitiveTypes.add(primitiveType);
        }

        primitiveTypes.sort(null);
        return primitiveTypes.get(primitiveTypes.size() - 1);
    }

    public enum Op {
        MULTIPLICATION(Associativity.LEFT, null, TypeCharacteristic.NUMERIC),
        DIVISION(Associativity.LEFT, null, TypeCharacteristic.NUMERIC),
        MODULO(Associativity.LEFT, null, TypeCharacteristic.INTEGER),
        ADDITION(Associativity.LEFT, null, TypeCharacteristic.NUMERIC),
        SUBTRACTION(Associativity.LEFT, null, TypeCharacteristic.NUMERIC),
        GREATER(null, PrimitiveType.BOOL, TypeCharacteristic.COMPARABLE),
        GREATER_OR_EQUAL(null, PrimitiveType.BOOL, TypeCharacteristic.COMPARABLE),
        LESS(null, PrimitiveType.BOOL, TypeCharacteristic.COMPARABLE),
        LESS_OR_EQUAL(null, PrimitiveType.BOOL, TypeCharacteristic.COMPARABLE),
        EQUAL(Associativity.LEFT, PrimitiveType.BOOL, TypeCharacteristic.CAN_EQUAL),
        NOT_EQUAL(Associativity.LEFT, PrimitiveType.BOOL, TypeCharacteristic.CAN_EQUAL),
        BITWISE_AND(Associativity.LEFT, null, TypeCharacteristic.INTEGER),
        BITWISE_XOR(Associativity.LEFT, null, TypeCharacteristic.INTEGER),
        BITWISE_OR(Associativity.LEFT, null, TypeCharacteristic.INTEGER),
        LOGICAL_AND(Associativity.LEFT, PrimitiveType.BOOL, TypeCharacteristic.BOOL),
        LOGICAL_OR(Associativity.LEFT, PrimitiveType.BOOL, TypeCharacteristic.BOOL),
        POW(Associativity.RIGHT, null, TypeCharacteristic.NUMERIC),
        BITWISE_SHIFT_LEFT(Associativity.LEFT, null, TypeCharacteristic.INTEGER),
        BITWISE_SHIFT_RIGHT(Associativity.LEFT, null, TypeCharacteristic.INTEGER);

        @Getter
        private final Associativity associativity;
        private final Set<TypeCharacteristic> requiredTypeCharacteristics;
        private final Type returnType;

        Op(Associativity associativity, Type returnType, TypeCharacteristic... characteristics) {
            this.associativity = associativity;
            this.returnType = returnType;
            this.requiredTypeCharacteristics = Collections.unmodifiableSet(EnumSet.of(characteristics[0], characteristics));
        }
    }

    public enum Associativity {
        LEFT, RIGHT
    }
}
