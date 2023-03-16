package com.codeforces.iomarkup.types.complex;

import com.codeforces.iomarkup.symbols.Symbol;
import com.codeforces.iomarkup.types.Type;
import com.codeforces.iomarkup.types.primitive.Char;
import com.codeforces.iomarkup.types.traits.LiteralType;

public class StringType extends ArrayType<Char> implements Symbol, LiteralType {
    private static final String SYMBOL_NAME = "string";
    private static final StringType INSTANCE = new StringType();

    private StringType() {
        super(Char.getInstance());
    }

    @Override
    public String getName() {
        return SYMBOL_NAME;
    }

    @Override
    public boolean isAssignableFrom(Type otherType) {
        if (Type.isAssignable(FixedLengthStringType.class, otherType))
            return true;
        return super.isAssignableFrom(otherType);
    }

    public static StringType getInstance() {
        return INSTANCE;
    }
}
