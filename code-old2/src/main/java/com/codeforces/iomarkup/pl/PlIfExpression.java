package com.codeforces.iomarkup.pl;

public class PlIfExpression extends PlExpression {
    private final PlExpression condition;
    private final PlExpression onTrue;
    private final PlExpression onFalse;

    public PlIfExpression(PlExpression condition, PlExpression onTrue, PlExpression onFalse) {
        this.condition = condition;
        this.onTrue = onTrue;
        this.onFalse = onFalse;
    }
}
