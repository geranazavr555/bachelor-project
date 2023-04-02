package com.codeforces.iomarkup.generation.impl.cpp;

import com.codeforces.iomarkup.generation.TargetLanguage;

import java.util.List;

public class CppTargetLanguage implements TargetLanguage {
    private static final String INDENT = "    ";

    public String getIndent() {
        return INDENT;
    }

    public String indent(int level) {
        return getIndent().repeat(level);
    }

    public List<String> indent(int level, List<String> content) {
        return content.stream().map(line -> indent(level)).toList();
    }

    public String getTypeName(String name) {
        return name + "_t";
    }
}
