package com.codeforces.iomarkup.pl;

import com.codeforces.iomarkup.scopes.ScopeResolvable;
import com.codeforces.iomarkup.symbols.SymbolsTable;
import com.codeforces.iomarkup.symbols.Variable;
import com.codeforces.iomarkup.types.Type;

import javax.annotation.Nullable;
import java.util.*;

public abstract class PlExpression implements ScopeResolvable {
    private final Set<Variable> bindedVars = new HashSet<>();
    private Type type;

    PlExpression() {
        this(null);
    }

    PlExpression(@Nullable Type type) {
        this.type = type;
    }

    public Optional<Type> getType() {
        return Optional.ofNullable(type);
    }

    public void addBindedVar(Variable variable) {
        bindedVars.add(variable);
    }

    public void addBindedVars(PlExpression childExpression) {
        childExpression.getBindedVars().forEach(this::addBindedVar);
    }

    public Set<Variable> getBindedVars() {
        return Collections.unmodifiableSet(bindedVars);
    }

    public Type ensureType() {
        if (type != null)
            return type;
        type = ensureTypeImpl();
        return type;
    }

    @Override
    public void resolveWithScope(SymbolsTable symbolsTable) {
        for (Variable bindedVar : bindedVars) {
            Type typeFromTable = symbolsTable.getVariable(bindedVar.getName())
                    .flatMap(Variable::getType)
                    .orElseThrow(RuntimeException::new); // todo
            bindedVar.setType(typeFromTable);
        }
    }

    protected abstract Type ensureTypeImpl();
}
