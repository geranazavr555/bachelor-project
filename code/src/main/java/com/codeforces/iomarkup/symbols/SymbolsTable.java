package com.codeforces.iomarkup.symbols;

import com.codeforces.iomarkup.symbols.struct.Struct;
import com.codeforces.iomarkup.types.Type;
import com.codeforces.iomarkup.types.complex.StringType;
import com.codeforces.iomarkup.types.primitive.PrimitiveTypes;

import java.util.*;

public class SymbolsTable {
    private final Map<String, Symbol> nameToSymbol = new LinkedHashMap<>();
    private final List<Symbol> visibilityStack = new ArrayList<>();
    private final List<Struct> scopeStack = new ArrayList<>();

    public SymbolsTable() {
        Arrays.stream(PrimitiveTypes.values())
                .map(PrimitiveTypes::get)
                .forEach(primitiveType -> nameToSymbol.put(primitiveType.getName(), primitiveType));

        var stringType = StringType.getInstance();
        nameToSymbol.put(stringType.getName(), stringType);
    }

    public Optional<Variable> getVariable(String name) {
        Symbol symbol = nameToSymbol.get(name);
        if (symbol instanceof Variable variable)
            return Optional.of(variable);
        return Optional.empty();
    }

    public Optional<Type> getType(String name) {
        Symbol symbol = nameToSymbol.get(name);
        if (symbol instanceof Type type)
            return Optional.of(type);
        return Optional.empty();
    }

    public void pushScope(Struct struct) {
        scopeStack.add(struct);
    }

    public void popScope() {
        scopeStack.remove(scopeStack.size() - 1);
    }

    public List<Struct> getScopePath() {
        return List.copyOf(scopeStack);
    }

    public void push(Symbol symbol) {
        if (nameToSymbol.containsKey(symbol.getName()))
            throw new RuntimeException(); // todo
        visibilityStack.add(symbol);
        nameToSymbol.put(symbol.getName(), symbol);
    }

    public void pop() {
        pop(1);
    }

    public void pop(int count) {
        var toPop = visibilityStack.subList(visibilityStack.size() - count, visibilityStack.size());
        toPop.stream().map(Symbol::getName).forEach(nameToSymbol::remove);
        toPop.clear();
    }
}
