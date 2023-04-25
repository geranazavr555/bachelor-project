package com.codeforces.iomarkup.generation.java;

import com.codeforces.iomarkup.generation.Translator;
import com.codeforces.iomarkup.type.PrimitiveType;
import com.codeforces.iomarkup.type.StringType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class JavaTargetTranslator implements Translator<String> {
    protected static final Map<String, String> predefinedTypes = Map.of(
            PrimitiveType.BOOL.getName(), "boolean",
            PrimitiveType.CHAR.getName(), "char",
            PrimitiveType.INT32.getName(), "int",
            PrimitiveType.UINT32.getName(), "long",
            PrimitiveType.INT64.getName(), "long",
            PrimitiveType.UINT64.getName(), "java.math.BigInteger",
            PrimitiveType.FLOAT32.getName(), "float",
            PrimitiveType.FLOAT64.getName(), "double",
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
