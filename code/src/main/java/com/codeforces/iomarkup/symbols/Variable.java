package com.codeforces.iomarkup.symbols;

import com.codeforces.iomarkup.scopes.ScopeResolvable;
import com.codeforces.iomarkup.symbols.struct.Struct;
import com.codeforces.iomarkup.types.Type;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class Variable extends AbstractSymbol implements ScopeResolvable {
    private Type type;
    private List<Struct> path;

    public Variable(String name) {
        this(name, null);
    }

    public Variable(String name, @Nullable Type type) {
        super(name);
        this.type = type;
        this.path = null;
    }

    public Optional<Type> getType() {
        return Optional.ofNullable(type);
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public void resolveWithScope(SymbolsTable symbolsTable) {
        path = symbolsTable.getScopePath();
    }
}
