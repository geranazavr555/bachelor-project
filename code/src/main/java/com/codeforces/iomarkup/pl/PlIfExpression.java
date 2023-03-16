package com.codeforces.iomarkup.pl;

import com.codeforces.iomarkup.types.Type;
import com.codeforces.iomarkup.types.primitive.Bool;

public class PlIfExpression extends PlExpression {
    private final PlExpression condition;
    private final PlExpression onTrue;
    private final PlExpression onFalse;

    public PlIfExpression(PlExpression condition, PlExpression onTrue, PlExpression onFalse) {
        this.condition = condition;
        this.onTrue = onTrue;
        this.onFalse = onFalse;
        addBindedVars(condition);
        addBindedVars(onTrue);
        addBindedVars(onFalse);
    }

    @Override
    protected Type ensureTypeImpl() {
        Type conditionType = condition.ensureType();
        if (!Bool.getInstance().isAssignableFrom(conditionType))
            throw new RuntimeException(); // todo

        Type onTrueType = onTrue.ensureType();
        Type onFalseType = onFalse.ensureType();
        if (!onTrueType.equals(onFalseType)) // todo
            throw new RuntimeException(); // todo

        return onTrueType;
    }
}
