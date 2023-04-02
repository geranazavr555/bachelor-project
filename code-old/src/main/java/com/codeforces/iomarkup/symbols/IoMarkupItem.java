package com.codeforces.iomarkup.symbols;

public abstract class IoMarkupItem extends AbstractSymbol {
//    private final Set<String> requiredSymbolNames = new LinkedHashSet<>();
//    private final Map<String, Variable> definedVariables = new LinkedHashMap<>();

    protected IoMarkupItem(String name) {
        super(name);
    }

//    public void addRequiredSymbol(String name) {
//        requiredSymbolNames.add(name);
//    }
//
//    public Set<String> getRequiredSymbolNames() {
//        return Collections.unmodifiableSet(requiredSymbolNames);
//    }
//
//    public void addDefinedVar(Variable variable) {
//        if (definedVariables.containsKey(variable.getName()))
//            throw new RuntimeException(); // todo
//        definedVariables.put(variable.getName(), variable);
//    }
//
//    public Map<String, Variable> getDefinedVariables() {
//        return Collections.unmodifiableMap(definedVariables);
//    }
}
