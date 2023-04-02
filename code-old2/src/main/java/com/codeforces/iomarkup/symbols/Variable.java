package com.codeforces.iomarkup.symbols;

import java.util.List;

public class Variable implements VariablePathElement {
    private final String name;
    private final List<VariablePathElement> path;

    public Variable(String name, List<VariablePathElement> path) {
        this.name = name;
        this.path = path;
    }
}
