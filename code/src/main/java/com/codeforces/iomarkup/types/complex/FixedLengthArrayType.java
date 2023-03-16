package com.codeforces.iomarkup.types.complex;

import com.codeforces.iomarkup.pl.PlExpression;
import com.codeforces.iomarkup.types.Type;
import com.codeforces.iomarkup.types.primitive.UInt32;
import com.codeforces.iomarkup.types.traits.ParametrizedType;

import java.util.List;

public class FixedLengthArrayType<T extends Type> extends ArrayType<T> implements ParametrizedType {
    private static final List<Type> typeParameters = List.of(UInt32.getInstance());
    private final PlExpression lengthExpression;

    public FixedLengthArrayType(T elementType, PlExpression lengthExpression) {
        super(elementType);
        this.lengthExpression = lengthExpression;
    }

    @Override
    public List<Type> getTypeParameters() {
        return typeParameters;
    }

    @Override
    public List<PlExpression> getActualTypeParameters() {
        return List.of(lengthExpression);
    }
}
