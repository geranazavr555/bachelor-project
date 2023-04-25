package com.codeforces.iomarkup.generation.python;

import com.codeforces.iomarkup.generation.Translator;
import com.codeforces.iomarkup.type.PrimitiveType;
import com.codeforces.iomarkup.type.StringType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

abstract class PythonTargetTranslator implements Translator<String> {
    protected static final Map<String, String> predefinedTypes = Map.of(
            PrimitiveType.BOOL.getName(), "bool",
            PrimitiveType.CHAR.getName(), "str",
            PrimitiveType.INT32.getName(), "int",
            PrimitiveType.UINT32.getName(), "int",
            PrimitiveType.INT64.getName(), "int",
            PrimitiveType.UINT64.getName(), "int",
            PrimitiveType.FLOAT32.getName(), "float",
            PrimitiveType.FLOAT64.getName(), "float",
            StringType.getInstance().getName(), "str"
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
