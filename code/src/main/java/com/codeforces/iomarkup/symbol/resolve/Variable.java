package com.codeforces.iomarkup.symbol.resolve;

import com.codeforces.iomarkup.pl.PlExpression;
import com.codeforces.iomarkup.symbol.Symbol;
import com.codeforces.iomarkup.type.Type;
import lombok.Getter;

public class Variable extends Symbol implements ConstructorItem {
    @Getter
    private final VariableDescription description;

    @Getter
    private final PlExpression constraint;

    public Variable(String name, Type type) {
        this(name, type, null);
    }

    public Variable(String name, VariableDescription description, PlExpression constraint) {
        super(name);
        this.description = description;
        this.constraint = constraint;
    }

    @Override
    public String toString() {
        return "Variable{" +
                "description=" + description +
                ", constraint=" + constraint +
                '}';
    }
}
