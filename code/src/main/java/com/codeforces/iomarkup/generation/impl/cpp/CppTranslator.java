package com.codeforces.iomarkup.generation.impl.cpp;

import com.codeforces.iomarkup.symbol.resolve.*;
import com.codeforces.iomarkup.type.PrimitiveType;
import com.codeforces.iomarkup.type.StringType;
import com.codeforces.iomarkup.type.Type;
import com.codeforces.iomarkup.type.TypeCharacteristic;

import java.util.*;
import java.util.stream.Stream;

public class CppTranslator {
    private static final Map<String, String> predefinedTypes = new HashMap<>();

    static {
        predefinedTypes.put(PrimitiveType.BOOL.getName(), "bool");
        predefinedTypes.put(PrimitiveType.CHAR.getName(), "char");
        predefinedTypes.put(PrimitiveType.INT32.getName(), "int");
        predefinedTypes.put(PrimitiveType.UINT32.getName(), "unsigned int");
        predefinedTypes.put(PrimitiveType.INT64.getName(), "long long");
        predefinedTypes.put(PrimitiveType.UINT64.getName(), "unsigned long long");
        predefinedTypes.put(PrimitiveType.FLOAT32.getName(), "float");
        predefinedTypes.put(PrimitiveType.FLOAT64.getName(), "double");
        predefinedTypes.put(StringType.getInstance().getName(), "std::string");
    }

    private final List<CppStructDeclaration> structs = new ArrayList<>();

    public List<CppStructDeclaration> getStructs() {
        return Collections.unmodifiableList(structs);
    }

    private String getTypeName(String structName) {
        return structName + "_t";
    }

    private List<CppVariableDeclaration> translateConstructorBody(List<ConstructorItem> body) {
        List<CppVariableDeclaration> fields = new ArrayList<>();
        for (ConstructorItem item : body) {
            if (item instanceof Variable variable) {
                fields.add(translateToVariableDeclaration(variable));
            } else if (item instanceof ConstructorIfAlt ifAlt)
                fields.addAll(translateToVariableDeclarations(ifAlt));
        }
        return fields;
    }

    public void translateToStructDeclaration(ConstructorWithBody constructor) {
        String name = getTypeName(constructor.getName());
        structs.add(new CppStructDeclaration(name, translateConstructorBody(constructor.getBody())));
    }

    private String getCppTypeStringFromType(Type type) {
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

    private String getCppTypeStringFromDescription(VariableDescription description, String varName) {
        if (description instanceof Type type) {
            return getCppTypeStringFromType(type);
        } else if (description instanceof ParametrizedDescription parametrizedDescription) {
            return getCppTypeStringFromType(parametrizedDescription.type());
        } else if (description instanceof NamedStructArrayDescription arrayDescription) {
            return "std::vector<" + getCppTypeStringFromDescription(arrayDescription.getComponent(), varName) + ">";
        } else if (description instanceof UnnamedStructArrayDescription arrayDescription) {
            List<CppVariableDeclaration> innerFields = translateConstructorBody(arrayDescription.getComponent());
            String typeName = getTypeName(varName);
            structs.add(new CppStructDeclaration(typeName, innerFields));
            return typeName;
        }
        throw new AssertionError();
    }

    private CppVariableDeclaration translateToVariableDeclaration(Variable variable) {
        return new CppVariableDeclaration(
                getCppTypeStringFromDescription(variable.getDescription(), variable.getName()),
                variable.getName()
        );
    }

    private List<CppVariableDeclaration> translateToVariableDeclarations(ConstructorIfAlt ifAlt) {
        List<CppVariableDeclaration> fields = new ArrayList<>();
        Stream.concat(ifAlt.getTrueItems().stream(), ifAlt.getFalseItems().stream()).forEach(item ->  {
            if (item instanceof Variable variable) {
                fields.add(translateToVariableDeclaration(variable));
            } else if (item instanceof ConstructorIfAlt innerAlt)
                fields.addAll(translateToVariableDeclarations(innerAlt));
        });
        return fields;
    }
}
