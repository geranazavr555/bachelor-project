package com.codeforces.iomarkup.generation.impl.cpp;

public record CppVariableDeclaration(String type, String name) {
    @Override
    public String toString() {
        return type + " " + name;
    }
}
