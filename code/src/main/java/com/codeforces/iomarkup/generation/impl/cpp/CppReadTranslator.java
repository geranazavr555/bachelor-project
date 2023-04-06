package com.codeforces.iomarkup.generation.impl.cpp;

import com.codeforces.iomarkup.pl.PlExpression;
import com.codeforces.iomarkup.symbol.resolve.*;
import com.codeforces.iomarkup.symbol.scope.Scope;
import com.codeforces.iomarkup.type.StructType;
import com.codeforces.iomarkup.type.Type;
import com.codeforces.iomarkup.type.TypeCharacteristic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CppReadTranslator {
    private final List<ConstructorWithBody> constructorStack = new ArrayList<>();
    private final List<Map<String, PlExpression>> parametersStack = new ArrayList<>();
    private final Scope globalScope;

    public CppReadTranslator(ConstructorWithBody constructor, Scope globalScope) {
        constructorStack.add(constructor);
        this.globalScope = globalScope;
    }

    private List<String> translate(VariableDescription description, String constructorName, String variableName) {
        List<String> result = new ArrayList<>();

        if (description instanceof Type type && TypeCharacteristic.NAMED.is(type)) {

            if (TypeCharacteristic.PREDEFINED.is(type)) {
                result.add("cin >> " + constructorName + "." + variableName + ";");
            } else if (TypeCharacteristic.STRUCT.is(type)) {
                constructorStack.add(globalScope.getConstructor(type.getName()));
                result.addAll(translate());
                constructorStack.remove(constructorStack.size() - 1);
            }

        } else if (description instanceof ParametrizedDescription parametrizedDescription) {

            if (!(parametrizedDescription.type() instanceof StructType structType))
                throw new AssertionError();

            ConstructorWithBody parametrizedConstructor = globalScope.getConstructor(structType.getName());
            Map<String, PlExpression> arguments = new HashMap<>();
            for (int i = 0; i < parametrizedDescription.arguments().size(); i++) {
                PlExpression actualArg = parametrizedDescription.arguments().get(i);
                String name = parametrizedConstructor.getArguments().get(i).getName();
                arguments.put(name, actualArg);
            }

            constructorStack.add(parametrizedConstructor);
            parametersStack.add(arguments);

            result.addAll(translate());

            parametersStack.remove(parametersStack.size() - 1);
            constructorStack.remove(constructorStack.size() - 1);

        } else if (description instanceof NamedTypeArrayDescription arrayDescription) {

            for (ArrayParameters arrayParameter : arrayDescription.getArrayParameters()) {
                var iName = arrayParameter.getIterationVarName().getName();
                var line = new StringBuilder("for (int ")
                        .append(iName)
                        .append(" = ")
                        .append(new CppPlExpressionTranslator(arrayParameter.getIterationStartExpression()).translate())
                        .append("; ")
                        .append(iName)
                        .append(" <= ")
                        .append(new CppPlExpressionTranslator(arrayParameter.getIterationStopExpression()).translate())
                        .append("; ")
                        .append(iName)
                        .append("++)");
                result.add(line.toString());
            }

            result.add("{");

            if (arrayDescription.getComponent() instanceof Type type) {
                if (TypeCharacteristic.PREDEFINED.is(type) && TypeCharacteristic.NAMED.is(type)) {
                    result.add(type.getName() + " " + "tmp;");
                    result.addAll(translate(type, null, "tmp"));
                    result.add(constructorName + "." + variableName + ".push_back(tmp);");
                } else throw new AssertionError();
            } else if (arrayDescription.getComponent() instanceof ParametrizedDescription parametrizedDescription) {
                result.addAll(translate(parametrizedDescription, constructorName, variableName)); // todo ?
                result.add(constructorName + "." + variableName + ".push_back(???);");
            } else throw new AssertionError();

            result.add("}");

        } else if (description instanceof UnnamedStructArrayDescription arrayDescription) {

            throw new RuntimeException(); // todo

        } else throw new AssertionError();

        return result;
    }

    public List<String> translate() {
        ConstructorWithBody constructor = constructorStack.get(constructorStack.size() - 1);
        List<String> result = new ArrayList<>();

        result.add(getTypeName(constructor.getName()) + ' ' + constructor.getName() + ';');
        StringBuilder line = new StringBuilder("cin ");

        for (var constructorItem : constructor.getBody()) {
            if (constructorItem instanceof Variable variable) {

                result.addAll(translate(variable.getDescription(), constructor.getName(), variable.getName()));

            } else if (constructorItem instanceof ConstructorIfAlt ifAlt) {
                throw new RuntimeException(); // todo
            }
        }
        return result;
    }

    private String getTypeName(String structName) {
        return structName + "_t";
    }
}
