package com.codeforces.iomarkup.types.primitive;

public class Bool extends PrimitiveType {
    private static final Bool INSTANCE = new Bool();

    private Bool() {
        super("bool");
    }

    public static Bool getInstance() {
        return INSTANCE;
    }
}
