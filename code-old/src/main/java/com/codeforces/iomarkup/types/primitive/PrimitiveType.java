package com.codeforces.iomarkup.types.primitive;

import com.codeforces.iomarkup.symbols.AbstractSymbol;
import com.codeforces.iomarkup.types.traits.LiteralType;

public abstract class PrimitiveType extends AbstractSymbol implements LiteralType {
    PrimitiveType(String name) {
        super(name);
    }
}
