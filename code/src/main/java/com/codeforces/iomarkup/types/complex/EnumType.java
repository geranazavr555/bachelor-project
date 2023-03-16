package com.codeforces.iomarkup.types.complex;

import com.codeforces.iomarkup.symbols.AbstractSymbol;
import com.codeforces.iomarkup.types.Type;
import com.codeforces.iomarkup.types.traits.LiteralType;
import lombok.Getter;

public class EnumType<T extends LiteralType> extends AbstractSymbol implements Type {
    @Getter
    private final T mappedType;

    public EnumType(String name, T mappedType) {
        super(name);
        this.mappedType = mappedType;
    }
}
