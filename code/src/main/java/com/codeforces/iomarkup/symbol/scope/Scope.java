package com.codeforces.iomarkup.symbol.scope;

import com.codeforces.iomarkup.symbol.Function;
import com.codeforces.iomarkup.symbol.resolve.ConstructorWithBody;
import com.codeforces.iomarkup.symbol.resolve.Variable;
import com.codeforces.iomarkup.type.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class Scope {
    private final ScopeImpl<NamedType> types;
    private final ScopeImpl<ConstructorWithBody> constructors;
    private final ScopeImpl<Variable> variables;
    private final ScopeImpl<Function> functions;

    public Scope() {
        this(null);
    }

    public Scope(Scope parent) {
        if (parent == null) {
            constructors = new ScopeImpl<>();
            variables = new ScopeImpl<>();

            types = new ScopeImpl<>();
            Stream.concat(
                    Stream.of(PrimitiveType.values()),
                    Stream.of(StringType.getInstance())
            ).map(NamedType::new).forEach(types::push);

            functions = new ScopeImpl<>();
            functions.push(new Function("len", PrimitiveType.INT32, List.of(TypeCharacteristic.ARRAY.closure())));
        } else {
            types = new ScopeImpl<>(parent.types);
            constructors = new ScopeImpl<>(parent.constructors);
            variables = new ScopeImpl<>(parent.variables);
            functions = new ScopeImpl<>(parent.functions);
        }
    }

    public boolean containsType(String name) {
        return types.contains(name);
    }

    public boolean containsConstructor(String name) {
        return constructors.contains(name);
    }

    public boolean containsVariable(String name) {
        return variables.contains(name);
    }

    public boolean containsFunction(String name) {
        return functions.contains(name);
    }

    public void pushType(NamedType type) {
        types.push(type);
    }

    public void pushConstructor(ConstructorWithBody constructor) {
        constructors.push(constructor);
    }

    public void pushVariable(Variable variable) {
        variables.push(variable);
    }

    public void pushFunction(Function function) {
        functions.push(function);
    }

    public Type getType(String name) {
        NamedType type = getNamedType(name);
        return type == null ? null : type.type();
    }

    public NamedType getNamedType(String name) {
        return types.get(name);
    }

    public ConstructorWithBody getConstructor(String name) {
        return constructors.get(name);
    }

    public Variable getVariable(String name) {
        return variables.get(name);
    }

    public Function getFunction(String name) {
        return functions.get(name);
    }

    public Set<ConstructorWithBody> getConstructors() {
        return Collections.unmodifiableSet(constructors.getAll());
    }

    public boolean containsName(String name) {
        return containsFunction(name) || containsType(name) || containsConstructor(name) || containsVariable(name);
    }

    public Scope newChild() {
        return new Scope(this);
    }

    @Override
    public String toString() {
        return "Scope{" +
                "types=" + types +
                ", constructors=" + constructors +
                ", variables=" + variables +
                ", functions=" + functions +
                '}';
    }
}