package com.codeforces.iomarkup.generation.python;

import com.codeforces.iomarkup.pl.PlExpression;
import com.codeforces.iomarkup.symbol.Symbol;
import com.codeforces.iomarkup.symbol.resolve.*;
import com.codeforces.iomarkup.symbol.scope.Scope;
import com.codeforces.iomarkup.type.StructType;
import com.codeforces.iomarkup.type.Type;
import com.codeforces.iomarkup.type.TypeCharacteristic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class PythonGraderWriteTranslator extends PythonTargetTranslator {
    private final Scope globalScope;
    private final Collection<ConstructorWithBody> constructors;
    private final List<ConstructorWithBody> delayedConstructors;

    public PythonGraderWriteTranslator(Scope globalScope, Collection<ConstructorWithBody> constructor) { // ok
        this.globalScope = globalScope;
        this.constructors = constructor;
        this.delayedConstructors = new ArrayList<>();
    }

    public List<String> translateToList() { // ok
        List<String> prototypes = new ArrayList<>();

        List<String> result = new ArrayList<>();
        for (ConstructorWithBody constructor : constructors) {
            result.add(generateSignature(constructor) + ":");
            result.addAll(indent(translateConstructor(constructor)));
            result.add("");
            result.add("");
        }

        while (!delayedConstructors.isEmpty()) {
            List<ConstructorWithBody> delayedConstructorsCopy = new ArrayList<>(delayedConstructors);
            delayedConstructors.clear();
            for (ConstructorWithBody constructor : delayedConstructorsCopy) {
                result.add(generateSignature(constructor) + ":");
                result.addAll(indent(translateConstructor(constructor)));
                result.add("");
                result.add("");
            }
        }

        prototypes.add("");
        prototypes.addAll(result);
        return prototypes;
    }

    private List<String> translateConstructorBody(String constructorName, List<ConstructorItem> constructorBody) { // ok
        List<String> result = new ArrayList<>();

        for (int i = 0; i < constructorBody.size(); ++i) {
            var item = constructorBody.get(i);
            if (item instanceof Variable variable) {
                result.addAll(translateVariable(constructorName, variable));
            } else if (item instanceof ConstructorIfAlt ifAlt) {
                result.addAll(translateIfAlt(constructorName, ifAlt));
            }

            if (item instanceof IoModifier ioModifier) {
                assert ioModifier == IoModifier.EOLN;
                result.add("print()");
            } else if (i + 1 < constructorBody.size() && !(constructorBody.get(i + 1) instanceof IoModifier)) {
                result.add("print(end=' ')");
            }
        }

        return result;
    }

    private List<String> translateConstructor(ConstructorWithBody constructor) { // ok
        return translateConstructorBody(constructor.getName(), constructor.getBody());
    }

    private List<String> translateSimpleType(String varLocate, Type type, List<PlExpression> args) { // ok
        List<String> result = new ArrayList<>();
        if (TypeCharacteristic.PREDEFINED.is(type)) {
            result.add("print(%s, end='')".formatted(varLocate));
        } else if (TypeCharacteristic.STRUCT.is(type)) {
            result.add("write_%s(%s)".formatted(
                    type.getName(),
                    Stream.concat(
                            Stream.of(varLocate),
                            args.stream().map(arg -> new PythonPlExpressionTranslator(arg).translate())
                    ).collect(Collectors.joining(", "))
            ));
        }
        return result;
    }

    private List<String> translateSimpleType(String constructorName, Variable variable, Type type, List<PlExpression> args) { // ok
        return translateSimpleType(constructorName + "." + variable.getName(), type, args);
    }

    private List<String> translateArray(String constructorName, String variableName,
                                        List<ArrayParameters> arrayParameters, VariableDescription component) { // ok
        List<String> result = new ArrayList<>();
        if (arrayParameters.size() != 1) throw new RuntimeException(); // todo
        ArrayParameters param = arrayParameters.get(0);
        result.add("for %s in range(%s, (%s) + 1):".formatted(
                param.getIterationVarName().getName(),
                new PythonPlExpressionTranslator(param.getIterationStartExpression()).translate(),
                new PythonPlExpressionTranslator(param.getIterationStopExpression()).translate()
        ));

        String typeName;
        List<PlExpression> args = List.of();
        Type type;
        if (component instanceof Type type1) {
            typeName = type1.getName();
            type = type1;
        } else if (component instanceof ParametrizedDescription parametrizedDescription) {
            typeName = parametrizedDescription.type().getName();
            args = parametrizedDescription.arguments();
            type = parametrizedDescription.type();
        } else throw new RuntimeException();

        result.addAll(indent(translateSimpleType(
                "%s.%s[(%s) - (%s)]".formatted(
                        constructorName,
                        variableName,
                        param.getIterationVarName().getName(),
                        new PythonPlExpressionTranslator(param.getIterationStartExpression()).translate()
                ),
                type, args)));

        for (ArrayParameters.IoSeparator ioSeparator : param.getSeparator()) {
            result.add(indent("print" + switch (ioSeparator) {
                case SPACE -> "(end=' ')";
                case EOLN -> "()";
            }));
        }

        return result;
    }

    private List<String> translateVariable(String constructorName, Variable variable) { // ok
        List<String> result = new ArrayList<>();
        if (variable.getDescription() instanceof Type type) {
            result.addAll(translateSimpleType(constructorName, variable, type, List.of()));
        } else if (variable.getDescription() instanceof ParametrizedDescription parametrizedDescription) {
            result.addAll(translateSimpleType(constructorName, variable, parametrizedDescription.type(),
                    parametrizedDescription.arguments()));
        } else if (variable.getDescription() instanceof NamedTypeArrayDescription arrayDescription) {
            result.addAll(translateArray(constructorName, variable.getName(), arrayDescription.getArrayParameters(),
                    arrayDescription.getComponent()));
        } else if (variable.getDescription() instanceof UnnamedStructArrayDescription arrayDescription) {
            result.addAll(translateArray(constructorName, variable.getName(), arrayDescription.getArrayParameters(),
                    new StructType(variable.getName(), false)));
            delayedConstructors.add(new ConstructorWithBody(variable.getName(), List.of(), arrayDescription.getComponent()));
        }

        return result;
    }

    private List<String> translateIfAlt(String constructorName, ConstructorIfAlt ifAlt) { // ok
        List<String> result = new ArrayList<>();
        result.add("if " + new PythonPlExpressionTranslator(ifAlt.getConditionalExpression(), constructorName).translate() + ":");

        result.addAll(indent(translateConstructorBody(constructorName, ifAlt.getTrueItems())));

        if (ifAlt.getFalseItems() != null && !ifAlt.getFalseItems().isEmpty()) {
            result.add("else:");
            result.addAll(indent(translateConstructorBody(constructorName, ifAlt.getFalseItems())));
        }
        return result;
    }

    private String getTypeName(String name) { // ok
        var r = predefinedTypes.get(name);
        return r != null ? r : name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    private String generateSignature(ConstructorWithBody constructor) { // ok
        return "def write_%s(%s)".formatted(
                constructor.getName(),
                Stream.concat(
                        Stream.of(constructor.getName()),
                        constructor.getArguments().stream().map(Symbol::getName)
                ).collect(Collectors.joining(", "))
        );
    }
}
