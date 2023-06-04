package com.codeforces.iomarkup.pl;

import com.codeforces.iomarkup.type.Type;

public abstract class PlExpression {
    public abstract Type getType();

    public void ensureType() {
        getType();
    }
}
