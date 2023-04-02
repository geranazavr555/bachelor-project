package com.codeforces.iomarkup.pl;

import com.codeforces.iomarkup.symbol.Function;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class PlFunctionCall extends PlExpression {
    private final Function function;
    private final List<PlExpression> argExpressions;
}
