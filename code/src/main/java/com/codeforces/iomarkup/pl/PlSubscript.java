package com.codeforces.iomarkup.pl;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PlSubscript extends PlExpression {
    private final PlExpression arrayExpression;
    private final PlExpression indexExpression;
}
