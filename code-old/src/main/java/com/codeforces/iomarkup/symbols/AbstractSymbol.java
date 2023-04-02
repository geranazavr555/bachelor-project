package com.codeforces.iomarkup.symbols;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

@AllArgsConstructor
public abstract class AbstractSymbol implements Symbol {
    @Getter
    private final String name;

    @Override
    public List<Symbol> getPath() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractSymbol that)) return false;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
