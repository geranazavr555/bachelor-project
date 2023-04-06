package com.codeforces.iomarkup.pl;

import com.codeforces.iomarkup.symbol.resolve.Variable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PlVarBinding extends PlExpression {
    private final Variable variable;
}