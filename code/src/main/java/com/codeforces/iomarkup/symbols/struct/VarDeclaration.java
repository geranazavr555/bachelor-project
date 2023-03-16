package com.codeforces.iomarkup.symbols.struct;

import com.codeforces.iomarkup.pl.PlExpression;
import com.codeforces.iomarkup.scopes.ScopeResolvable;
import com.codeforces.iomarkup.symbols.Symbol;
import com.codeforces.iomarkup.symbols.SymbolsTable;
import com.codeforces.iomarkup.symbols.Variable;
import com.codeforces.iomarkup.types.Type;
import com.codeforces.iomarkup.types.primitive.Bool;
import com.codeforces.iomarkup.types.traits.ParametrizedType;
import com.codeforces.iomarkup.types.unknown.UnknownParametrizedType;
import com.codeforces.iomarkup.types.unknown.UnknownType;

import javax.annotation.Nullable;

public class VarDeclaration implements StructItem, ScopeResolvable {
    private final Variable variable;

    @Nullable
    private final PlExpression condition;

    public VarDeclaration(Variable variable, @Nullable PlExpression condition) {
        this.variable = variable;
        this.condition = condition;
    }

    public Variable getVariable() {
        return variable;
    }

    @Override
    public void resolveWithScope(SymbolsTable symbolsTable) {
        Type type = variable.getType().orElseThrow(RuntimeException::new); // todo
        if (type instanceof UnknownType unknownType) {
            variable.setType(symbolsTable.getType(unknownType.getName()).orElseThrow(RuntimeException::new)); // todo
        } else if (type instanceof UnknownParametrizedType unknownParametrizedType) {
            Type typeFromTable = symbolsTable.getType(unknownParametrizedType.getName())
                    .orElseThrow(RuntimeException::new);
            if (!(typeFromTable instanceof ParametrizedType parametrizedTypeFromTable))
                throw new RuntimeException(); // todo
            parametrizedTypeFromTable.resolveWithScope(symbolsTable);
            variable.setType(parametrizedTypeFromTable);
        } else if (type instanceof Symbol typeSymbol) {
            Type typeFromTable = symbolsTable.getType(typeSymbol.getName()).orElseThrow(RuntimeException::new);
            if (!type.isAssignableFrom(typeFromTable))
                throw new RuntimeException(); // todo
        } else
            throw new RuntimeException(); // todo

        variable.resolveWithScope(symbolsTable);

        if (condition != null) {
            condition.resolveWithScope(symbolsTable);
            if (!Bool.getInstance().isAssignableFrom(condition.ensureType()))
                throw new RuntimeException();
        }
    }
}
