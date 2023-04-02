package com.codeforces.iomarkup.pl;

import com.codeforces.iomarkup.type.Type;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PlValue<T> extends PlExpression {
    private final Type type;
    private final T value;
}
