package com.codeforces.iomarkup.generation.impl.cpp;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class CppStructDeclaration {
    private final String name;
    private final List<CppVariableDeclaration> fields;

    @Override
    public String toString() {
        return """
                struct %s
                {
                    %s
                };
                """.formatted(name, formatFields());
    }

    private CharSequence formatFields() {
        var sb = new StringBuilder();
        fields.forEach(field -> sb.append(field).append(";\n"));
        return sb;
    }
}
