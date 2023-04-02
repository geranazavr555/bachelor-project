package com.codeforces.iomarkup.symbol.scope;

import com.codeforces.iomarkup.symbol.Symbol;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ScopeImpl<T extends Symbol> {
    private final ScopeImpl<T> parent;
    private final Map<String, T> nameToSymbol = new HashMap<>();

    public ScopeImpl() {
        this(null);
    }

    public ScopeImpl(ScopeImpl<T> parent) {
        this.parent = parent;
    }

    public boolean contains(T symbol) {
        return contains(symbol.getName());
    }

    public boolean contains(String symbolName) {
        return nameToSymbol.containsKey(symbolName) || parent != null && parent.contains(symbolName);
    }

    public void push(T symbol) {
        if (nameToSymbol.containsKey(symbol.getName()))
            throw new RuntimeException(); // todo
        nameToSymbol.put(symbol.getName(), symbol);
    }

    public T get(String name) {
        T result = nameToSymbol.get(name);
        return result == null && parent != null ? parent.get(name) : result;
    }

    public Set<T> getAll() {
        Set<T> values = new HashSet<>(nameToSymbol.values());
        if (parent != null)
            values.addAll(parent.getAll());
        return values;
    }

    @Override
    public String toString() {
        return "ScopeImpl{" +
                "parent=" + parent +
                ", nameToSymbol=" + nameToSymbol +
                '}';
    }
}
