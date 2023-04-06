package com.codeforces.iomarkup.pl;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PlIfOperator extends PlExpression {
    private final PlExpression condition;
    private final PlExpression trueExpression;
    private final PlExpression falseExpression;
}
