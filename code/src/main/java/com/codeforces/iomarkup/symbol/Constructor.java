package com.codeforces.iomarkup.symbol;

import lombok.Getter;

import java.util.List;

public abstract class Constructor extends Symbol {
    @Getter
    private final List<ConstructorArgument> arguments;

    protected Constructor(String name) {
        this(name, List.of());
    }

    protected Constructor(String name, List<ConstructorArgument> arguments) {
        super(name);
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        return "Constructor{" +
                "name=" + getName() + ", " +
                "arguments=" + arguments +
                '}';
    }
}
