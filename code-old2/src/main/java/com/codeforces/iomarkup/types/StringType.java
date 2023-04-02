package com.codeforces.iomarkup.types;

import com.codeforces.iomarkup.pl.PlExpression;

import javax.annotation.Nullable;

import static com.codeforces.iomarkup.types.TypeCharacteristic.*;

public class StringType extends ArrayType {
    public StringType() {
        this(null);
    }

    public StringType(@Nullable PlExpression lengthExpression) {
        super(PrimitiveType.CHAR, lengthExpression);
        this.characteristics.add(LITERAL);
        this.characteristics.add(COMPARABLE);
        this.characteristics.add(NAMED);
        this.characteristics.add(CAN_EQUAL);
    }

    @Override
    public String getName() {
        return "string";
    }
}
