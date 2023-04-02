package com.codeforces.iomarkup.pl;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PlIfOperator extends PlExpression {
    private final PlExpression condition;
    private final PlExpression trueExpression;
    private final PlExpression falseExpression;
}
