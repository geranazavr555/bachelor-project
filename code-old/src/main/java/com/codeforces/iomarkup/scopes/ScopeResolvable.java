package com.codeforces.iomarkup.scopes;

import com.codeforces.iomarkup.symbols.SymbolsTable;

public interface ScopeResolvable {
    void resolveWithScope(SymbolsTable symbolsTable);
}
