package com.codeforces.iomarkup.pl;

import com.codeforces.iomarkup.types.complex.ArrayType;
import com.codeforces.iomarkup.types.Type;
import com.codeforces.iomarkup.types.primitive.IntegerPrimitiveType;

public class PlSubscript extends PlExpression {
    private final PlExpression array;
    private final PlExpression index;

    public PlSubscript(PlExpression array, PlExpression index) {
        this.array = array;
        this.index = index;
        addBindedVars(array);
        addBindedVars(index);

        if (array.getType().isPresent() && index.getType().isPresent())
            ensureType();
    }

    @Override
    public Type ensureTypeImpl() {
        index.ensureType();
        ensureIndexTypeCorrect();

        Type arrayType = array.ensureType();
        ensureArrayTypeCorrect();
        return ((ArrayType<?>) arrayType).getElementType();
    }

    private void ensureIndexTypeCorrect() {
        //noinspection OptionalGetWithoutIsPresent
        Type indexType = index.getType().get();
        if (!Type.isAssignable(IntegerPrimitiveType.class, indexType))
            throw new RuntimeException(); // todo
    }

    private void ensureArrayTypeCorrect() {
        //noinspection OptionalGetWithoutIsPresent
        Type arrayType = array.getType().get();
        if (!Type.isAssignable(ArrayType.class, arrayType))
            throw new RuntimeException(); // todo
    }
}
