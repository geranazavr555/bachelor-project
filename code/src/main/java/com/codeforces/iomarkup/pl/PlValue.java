package com.codeforces.iomarkup.pl;

import com.codeforces.iomarkup.type.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PlValue<T> extends PlExpression {
    private final Type type;
    private final T value;
}
