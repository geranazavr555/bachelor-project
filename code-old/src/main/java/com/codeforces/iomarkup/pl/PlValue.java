package com.codeforces.iomarkup.pl;

import com.codeforces.iomarkup.types.traits.LiteralType;
import com.codeforces.iomarkup.types.Type;

public class PlValue<T extends LiteralType, V> extends PlExpression {
    private final V value;

    public PlValue(T type, V value) {
        super(type);
        this.value = value;
    }

    @Override
    public Type ensureTypeImpl() {
        //noinspection OptionalGetWithoutIsPresent
        return getType().get();
    }
}
