package com.codeforces.iomarkup.symbol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class Symbol {
    private final String name;
    private final List<Symbol> requires;

    protected Symbol(String name) {
        this.name = name;
        this.requires = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void addRequirement(Symbol symbol) {
        requires.add(symbol);
    }

    public List<Symbol> getRequirements() {
        return Collections.unmodifiableList(requires);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Symbol symbol)) return false;
        return name.equals(symbol.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
