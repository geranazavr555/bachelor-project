package com.codeforces.iomarkup.pl;

import com.codeforces.iomarkup.type.PrimitiveType;
import com.codeforces.iomarkup.type.Type;
import com.codeforces.iomarkup.type.TypeCharacteristic;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PlIfOperator extends PlExpression {
    private final PlExpression condition;
    private final PlExpression trueExpression;
    private final PlExpression falseExpression;

    @Override
    public Type getType() {
        var conditionType = condition.getType();
        if (!TypeCharacteristic.BOOL.is(conditionType))
            throw new RuntimeException();

        var trueType = trueExpression.getType();
        var falseType = falseExpression.getType();

        if (!trueType.isAssignableFrom(falseType) || !falseType.isAssignableFrom(trueType))
            throw new RuntimeException();

        if (trueType instanceof PrimitiveType primitiveTrueType &&
            falseType instanceof PrimitiveType primitiveFalseType) {
            if (primitiveTrueType.compareTo(primitiveFalseType) >= 0)
                return trueType;
            else
                return falseType;
        }

        return trueType;
    }
}
