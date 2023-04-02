package com.codeforces.iomarkup.types.traits;

import com.codeforces.iomarkup.pl.PlExpression;
import com.codeforces.iomarkup.scopes.ScopeResolvable;
import com.codeforces.iomarkup.symbols.SymbolsTable;
import com.codeforces.iomarkup.types.Type;

import java.util.List;

public interface ParametrizedType extends Type, ScopeResolvable {
    List<Type> getTypeParameters();
    List<PlExpression> getActualTypeParameters();

    @Override
    default void resolveWithScope(SymbolsTable symbolsTable) {
        var types = getTypeParameters();
        var actualParams = getActualTypeParameters();
        if (types.size() != actualParams.size())
            throw new RuntimeException(); // todo
        for (int i = 0; i < types.size(); i++) {
            var type = types.get(i);
            var param = actualParams.get(i);
            param.resolveWithScope(symbolsTable);
            if (!type.isAssignableFrom(param.ensureType()))
                throw new RuntimeException(); // todo
        }
    }
}
