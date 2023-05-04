package com.codeforces.iomarkup.generation.java;

import com.codeforces.iomarkup.symbol.Symbol;
import com.codeforces.iomarkup.symbol.resolve.*;
import com.codeforces.iomarkup.symbol.scope.Scope;
import com.codeforces.iomarkup.type.Type;
import com.codeforces.iomarkup.type.TypeCharacteristic;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class JavaStructDeclarationsTranslator extends JavaTargetTranslator {
    private final Set<String> translatedStructs = new HashSet<>();
    private final List<List<String>> structs = new ArrayList<>();
    private final Scope globalScope;

    public JavaStructDeclarationsTranslator(Scope globalScope) {
        this.globalScope = globalScope;
    }

    public List<String> translateToList() {
        for (ConstructorWithBody constructor : globalScope.getConstructors()) {
            if (!translatedStructs.contains(constructor.getName()))
                structs.add(translate(constructor));
        }
        return indent(structs.stream().flatMap(Collection::stream).collect(Collectors.toList()));
    }

    private List<String> translate(ConstructorWithBody constructor) {
        List<String> result = new ArrayList<>();
        translatedStructs.add(constructor.getName());
        for (Symbol symbol : constructor.getRequirements()) {
            if (symbol instanceof ConstructorWithBody requiredConstructor
                    && !translatedStructs.contains(requiredConstructor.getName())) {
                result.addAll(translate(requiredConstructor));
            }
        }
        result.addAll(translateToStructDeclaration(constructor));
        result.add("");
        return result;
    }

    private String getTypeName(String structName) {
        return structName.substring(0, 1).toUpperCase() + structName.substring(1);
    }

    private List<String> translateConstructorBody(List<ConstructorItem> body) {
        List<String> result = new ArrayList<>();
        for (ConstructorItem item : body) {
            if (item instanceof Variable variable) {
                result.add(indent(translateToVariableDeclaration(variable)));
            } else if (item instanceof ConstructorIfAlt ifAlt)
                result.addAll(indent(translateToVariableDeclarations(ifAlt)));
        }
        return result;
    }

    private List<String> translateToStructDeclaration(ConstructorWithBody constructor) {
        String name = getTypeName(constructor.getName());
        List<String> result = new ArrayList<>();
        result.add("private static class " + name + " {");
        result.addAll(translateConstructorBody(constructor.getBody()));
        result.add("};");
        return result;
    }

    private String getTypeStringFromType(Type type) {
        if (TypeCharacteristic.NAMED.is(type))
            if (TypeCharacteristic.PREDEFINED.is(type))
                return predefinedTypes.get(type.getName());
            else if (TypeCharacteristic.STRUCT.is(type))
                return getTypeName(type.getName());
            else
                throw new AssertionError();
        else
            throw new AssertionError();
    }

    private String getJavaTypeStringFromDescription(VariableDescription description, String varName) {
        if (description instanceof Type type) {
            return getTypeStringFromType(type);
        } else if (description instanceof ParametrizedDescription parametrizedDescription) {
            return getTypeStringFromType(parametrizedDescription.type());
        } else if (description instanceof NamedTypeArrayDescription arrayDescription) {
            return "java.util.List<" + getJavaTypeStringFromDescription(arrayDescription.getComponent(), varName) + ">";
        } else if (description instanceof UnnamedStructArrayDescription arrayDescription) {
            List<String> innerFields = translateConstructorBody(arrayDescription.getComponent());
            String typeName = getTypeName(varName);

            List<String> innerStruct = new ArrayList<>();
            innerStruct.add("private static class " + typeName + " {");
            innerStruct.addAll(indent(innerFields));
            innerStruct.add("};");
            innerStruct.add("");

            structs.add(innerStruct);
            return "java.util.List<" + typeName + ">";
        }
        throw new AssertionError();
    }

    private String translateToVariableDeclaration(Variable variable) {
        boolean array = variable.getDescription() instanceof ArrayDescription<?>;
        return "private " + getJavaTypeStringFromDescription(variable.getDescription(), variable.getName())
                + " " +  variable.getName() + (array ? " = new java.util.ArrayList<>()" : "") + ";";
    }

    private List<String> translateToVariableDeclarations(ConstructorIfAlt ifAlt) {
        List<String> result = new ArrayList<>();
        Stream.concat(ifAlt.getTrueItems().stream(), ifAlt.getFalseItems().stream()).forEach(item ->  {
            if (item instanceof Variable variable) {
                result.add(translateToVariableDeclaration(variable));
            } else if (item instanceof ConstructorIfAlt innerAlt)
                result.addAll(translateToVariableDeclarations(innerAlt));
        });
        return result;
    }

    @Override
    public String translate() {
        return String.join("\n", translateToList());
    }
}
