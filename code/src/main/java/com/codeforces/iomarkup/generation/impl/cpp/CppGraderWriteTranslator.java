package com.codeforces.iomarkup.generation.impl.cpp;

import com.codeforces.iomarkup.pl.PlExpression;
import com.codeforces.iomarkup.symbol.resolve.*;
import com.codeforces.iomarkup.symbol.scope.Scope;
import com.codeforces.iomarkup.type.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CppGraderWriteTranslator extends CppTargetTranslator {
    private final Scope globalScope;
    private final Collection<ConstructorWithBody> constructors;
    private final List<ConstructorWithBody> delayedConstructors;

    public CppGraderWriteTranslator(Scope globalScope, Collection<ConstructorWithBody> constructor) { // ok
        this.globalScope = globalScope;
        this.constructors = constructor;
        this.delayedConstructors = new ArrayList<>();
    }

    public List<String> translateToList() { // ok
        List<String> prototypes = new ArrayList<>();
        for (ConstructorWithBody constructor : constructors) {
            prototypes.add(generateSignature(constructor) + ";");
        }
        incIndentLevel();

        List<String> result = new ArrayList<>();
        for (ConstructorWithBody constructor : constructors) {
            result.add(generateSignature(constructor));
            result.add("{");
            result.addAll(indent(translateConstructor(constructor)));
            result.add("}");
            result.add("");
        }

        while (!delayedConstructors.isEmpty()) {
            List<ConstructorWithBody> delayedConstructorsCopy = new ArrayList<>(delayedConstructors);
            delayedConstructors.clear();
            for (ConstructorWithBody constructor : delayedConstructorsCopy) {
                prototypes.add(generateSignature(constructor) + ";");

                result.add(generateSignature(constructor));
                result.add("{");
                result.addAll(indent(translateConstructor(constructor)));
                result.add("}");
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
                result.add("cout << endl;");
            } else if (i + 1 < constructorBody.size() && !(constructorBody.get(i + 1) instanceof IoModifier)) {
                result.add("cout << ' ';");
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
            result.add("cout << %s;".formatted(varLocate));
        } else if (TypeCharacteristic.STRUCT.is(type)) {
            result.add("write_%s(%s);".formatted(
                    type.getName(),
                    Stream.concat(
                            Stream.of(varLocate),
                            args.stream().map(arg -> new CppPlExpressionTranslator(arg).translate())
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
        result.add("for (%s %s = %s; %s <= %s; %s++)".formatted(
                getTypeName(((Type) param.getIterationVarName().getDescription()).getName()),
                param.getIterationVarName().getName(),
                new CppPlExpressionTranslator(param.getIterationStartExpression()).translate(),
                param.getIterationVarName().getName(),
                new CppPlExpressionTranslator(param.getIterationStopExpression()).translate(),
                param.getIterationVarName().getName()
        ));
        result.add("{");

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
                        new CppPlExpressionTranslator(param.getIterationStartExpression()).translate()
                ),
                type, args)));

        for (ArrayParameters.IoSeparator ioSeparator : param.getSeparator()) {
            result.add(indent("cout << " + switch (ioSeparator) {
                case SPACE -> "\" \"";
                case EOLN -> "endl";
            } + ";"));
        }

        result.add("}");
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
        result.add("if (" + new CppPlExpressionTranslator(ifAlt.getConditionalExpression(), constructorName).translate() + ")");
        result.add("{");

        result.addAll(indent(translateConstructorBody(constructorName, ifAlt.getTrueItems())));

        result.add("}");
        if (ifAlt.getFalseItems() != null && !ifAlt.getFalseItems().isEmpty()) {
            result.add("else");
            result.add("{");

            result.addAll(indent(translateConstructorBody(constructorName, ifAlt.getFalseItems())));

            result.add("}");
        }
        return result;
    }

    private String getTypeName(String name) { // ok
        var result = predefinedTypes.get(name);
        return result == null ? name + "_t" : result;
    }

    private String generateSignature(ConstructorWithBody constructor) { // ok
        return "void write_%s(%s)".formatted(
                constructor.getName(),
                Stream.concat(
                        Stream.of(getTypeName(constructor.getName()) + " const& " + constructor.getName()),
                        constructor.getArguments().stream().map(arg -> {
                            var type = globalScope.getNamedType(arg.getType()).type();
                            if (TypeCharacteristic.PREDEFINED.is(type))
                                return predefinedTypes.get(type.getName()) + " " + arg.getName();
                            else if (TypeCharacteristic.STRUCT.is(type))
                                return getTypeName(type.getName()) + " const& " + arg.getName();
                            else throw new AssertionError();
                        })
                ).collect(Collectors.joining(", "))
        );
    }
}
