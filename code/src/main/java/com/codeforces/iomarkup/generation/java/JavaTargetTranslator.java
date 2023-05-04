package com.codeforces.iomarkup.generation.java;

import com.codeforces.iomarkup.generation.Translator;
import com.codeforces.iomarkup.type.PrimitiveType;
import com.codeforces.iomarkup.type.StringType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class JavaTargetTranslator implements Translator<String> {
    protected static final Map<String, String> predefinedTypes = Map.of(
            PrimitiveType.BOOL.getName(), "Boolean",
            PrimitiveType.CHAR.getName(), "Character",
            PrimitiveType.INT32.getName(), "Integer",
            PrimitiveType.UINT32.getName(), "Long",
            PrimitiveType.INT64.getName(), "Long",
            PrimitiveType.UINT64.getName(), "java.math.BigInteger",
            PrimitiveType.FLOAT32.getName(), "Float",
            PrimitiveType.FLOAT64.getName(), "Double",
            StringType.getInstance().getName(), "java.lang.String"
    );

    public String translate() {
        return String.join("\n", translateToList());
    }

    public List<String> translateToList() {
        return List.of(translate());
    }

    protected List<String> indent(List<String> lines) {
        return lines.stream().map(this::indent).collect(Collectors.toList());
    }

    protected String indent(String line) {
        return "    ".repeat(1) + line;
    }
}
