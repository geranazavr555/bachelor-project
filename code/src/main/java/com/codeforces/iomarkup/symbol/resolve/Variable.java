package com.codeforces.iomarkup.symbol.resolve;

import com.codeforces.iomarkup.pl.PlExpression;
import com.codeforces.iomarkup.symbol.Symbol;
import com.codeforces.iomarkup.type.Type;
import lombok.Getter;

import java.util.List;

public class Variable extends Symbol implements ConstructorItem {
    @Getter
    private final VariableDescription description;

    @Getter
    private final PlExpression constraint;

    @Getter
    private final List<VarPathItem> path;

    public Variable(String name, Type type, List<VarPathItem> path) {
        this(name, type, null, path);
    }

    public Variable(String name, VariableDescription description, PlExpression constraint, List<VarPathItem> path) {
        super(name);
        this.description = description;
        this.constraint = constraint;
        this.path = path;
    }

    @Override
    public String toString() {
        return "Variable{" +
                "description=" + description +
                ", constraint=" + constraint +
                '}';
    }
}
