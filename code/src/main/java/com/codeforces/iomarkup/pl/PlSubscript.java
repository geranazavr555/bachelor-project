package com.codeforces.iomarkup.pl;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PlSubscript extends PlExpression {
    private final PlExpression arrayExpression;
    private final PlExpression indexExpression;
}
