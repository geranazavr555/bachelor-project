package com.codeforces.iomarkup.types.complex;

import com.codeforces.iomarkup.types.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ArrayType<T extends Type> implements Type {
    @Getter
    private final T elementType;
}
