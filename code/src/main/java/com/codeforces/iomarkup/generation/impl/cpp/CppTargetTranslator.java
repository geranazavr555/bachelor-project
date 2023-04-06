package com.codeforces.iomarkup.generation.impl.cpp;

import com.codeforces.iomarkup.type.PrimitiveType;
import com.codeforces.iomarkup.type.StringType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class CppTargetTranslator implements Translator {
    protected static final Map<String, String> predefinedTypes = Map.of(
            PrimitiveType.BOOL.getName(), "bool",
            PrimitiveType.CHAR.getName(), "char",
            PrimitiveType.INT32.getName(), "int",
            PrimitiveType.UINT32.getName(), "unsigned int",
            PrimitiveType.INT64.getName(), "long long",
            PrimitiveType.UINT64.getName(), "unsigned long long",
            PrimitiveType.FLOAT32.getName(), "float",
            PrimitiveType.FLOAT64.getName(), "double",
            StringType.getInstance().getName(), "std::string"
    );

    private int indentLevel = 0;

    public String translate() {
        return String.join("\n", translateToList());
    }

    public List<String> translateToList() {
        return List.of(translate());
    }

    protected void incIndentLevel() {
        indentLevel++;
    }

    protected void decIndentLevel() {
        indentLevel--;
    }

    protected List<String> indent(List<String> lines) {
        return lines.stream().map(this::indent).collect(Collectors.toList());
    }

    protected String indent(String line) {
        return "    ".repeat(indentLevel) + line;
    }
}
