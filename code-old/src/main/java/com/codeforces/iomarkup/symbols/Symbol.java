package com.codeforces.iomarkup.symbols;

public interface Symbol {
    String getName();

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();
}
