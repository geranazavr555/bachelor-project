package com.codeforces.iomarkup.pl;

import com.codeforces.iomarkup.type.ArrayType;
import com.codeforces.iomarkup.type.Type;
import com.codeforces.iomarkup.type.TypeCharacteristic;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PlSubscript extends PlExpression {
    private final PlExpression arrayExpression;
    private final PlExpression indexExpression;

    @Override
    public Type getType() {
        var arrayType = arrayExpression.getType();
        if (!TypeCharacteristic.ARRAY.is(arrayType))
            throw new RuntimeException();

        var indexType = indexExpression.getType();
        if (!TypeCharacteristic.INTEGER.is(indexType))
            throw new RuntimeException();

        if (arrayType instanceof ArrayType arrayType1)
            return arrayType1.getComponentType();

        throw new RuntimeException();
    }
}
