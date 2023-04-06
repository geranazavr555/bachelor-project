package com.codeforces.iomarkup.symbol.resolve;

import com.codeforces.iomarkup.pl.PlExpression;
import lombok.Getter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class ArrayParameters {
    private final Variable iterationVarName;
    private final PlExpression iterationStartExpression;
    private final PlExpression iterationStopExpression;
    private final List<IoSeparator> separator;

    public ArrayParameters(Variable iterationVarName,
                           PlExpression iterationStartExpression,
                           PlExpression iterationStopExpression,
                           List<IoSeparator> separator) {
        this.iterationVarName = iterationVarName;
        this.iterationStartExpression = iterationStartExpression;
        this.iterationStopExpression = iterationStopExpression;
        this.separator = separator;
        assert Set.of(IoSeparator.EOLN).equals(new HashSet<>(separator)) || List.of(IoSeparator.SPACE).equals(separator);
    }

    public enum IoSeparator {
        EOLN, SPACE;
    }
}
