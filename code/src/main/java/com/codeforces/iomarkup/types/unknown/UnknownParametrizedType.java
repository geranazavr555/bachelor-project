package com.codeforces.iomarkup.types.unknown;

import com.codeforces.iomarkup.pl.PlExpression;
import com.codeforces.iomarkup.symbols.AbstractSymbol;
import com.codeforces.iomarkup.symbols.SymbolsTable;
import com.codeforces.iomarkup.types.Type;
import com.codeforces.iomarkup.types.traits.ParametrizedType;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class UnknownParametrizedType extends AbstractSymbol implements ParametrizedType {
    private final List<PlExpression> typeParameterExpressions;

    @Nullable
    private List<Type> typeParameterTypes;

    public UnknownParametrizedType(String name, List<PlExpression> typeParameterExpressions) {
        super(name);
        this.typeParameterExpressions = typeParameterExpressions;
    }

    @Override
    public List<Type> getTypeParameters() {
        return Collections.unmodifiableList(Objects.requireNonNull(typeParameterTypes));
    }

    @Override
    public List<PlExpression> getActualTypeParameters() {
        throw new UnsupportedOperationException(); // todo
    }

    @Override
    public void resolveWithScope(SymbolsTable symbolsTable) {
        throw new UnsupportedOperationException(); // todo
    }
}
