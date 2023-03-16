package com.codeforces.iomarkup.types.complex;

import com.codeforces.iomarkup.pl.PlExpression;
import com.codeforces.iomarkup.symbols.Symbol;
import com.codeforces.iomarkup.types.primitive.Char;
import com.codeforces.iomarkup.types.traits.ComparableType;

public class FixedLengthStringType extends FixedLengthArrayType<Char> implements Symbol, ComparableType {
    private static final String SYMBOL_NAME = "string";

    public FixedLengthStringType(PlExpression lengthExpression) {
        super(Char.getInstance(), lengthExpression);
    }

    @Override
    public String getName() {
        return SYMBOL_NAME;
    }
}
