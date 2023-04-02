package com.codeforces.iomarkup.types.primitive;

public class Char extends PrimitiveType {
    private static final Char INSTANCE = new Char();

    private Char() {
        super("char");
    }

    public static Char getInstance() {
        return INSTANCE;
    }
}
