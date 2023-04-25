package com.codeforces.iomarkup.generation.python;

import com.codeforces.iomarkup.symbol.Symbol;
import com.codeforces.iomarkup.symbol.resolve.*;
import com.codeforces.iomarkup.symbol.scope.Scope;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class PythonStructDeclarationsTranslator extends PythonTargetTranslator {
    private final Set<String> translatedStructs = new HashSet<>();
    private final List<List<String>> structs = new ArrayList<>();
    private final Scope globalScope;

    public PythonStructDeclarationsTranslator(Scope globalScope) {
        this.globalScope = globalScope;
    }

    public List<String> translateToList() {
        for (ConstructorWithBody constructor : globalScope.getConstructors()) {
            if (!translatedStructs.contains(constructor.getName()))
                structs.add(translate(constructor));
        }
        return structs.stream().flatMap(Collection::stream).collect(Collectors.toList());
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
        result.add("class " + name + ":");
        result.add(indent("def __init__(self):"));
        result.addAll(indent(translateConstructorBody(constructor.getBody())));
        return result;
    }

    private void possiblyTranslateSubstructures(VariableDescription description, String varName) {
        if (description instanceof NamedTypeArrayDescription arrayDescription) {
            possiblyTranslateSubstructures(arrayDescription.getComponent(), varName);
        } else if (description instanceof UnnamedStructArrayDescription arrayDescription) {
            List<String> innerFields = translateConstructorBody(arrayDescription.getComponent());
            String typeName = getTypeName(varName);

            List<String> innerStruct = new ArrayList<>();
            innerStruct.add("class " + typeName + ":");
            innerStruct.add(indent("def __init__(self):"));
            innerStruct.addAll(indent(innerFields));
            innerStruct.add("");
            innerStruct.add("");

            structs.add(innerStruct);
        }
    }

    private String translateToVariableDeclaration(Variable variable) {
        possiblyTranslateSubstructures(variable.getDescription(), variable.getName());
        return "self.%s = %s".formatted(
                variable.getName(),
                variable.getDescription() instanceof ArrayDescription<?> ? "[]" : "None"
        );
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
