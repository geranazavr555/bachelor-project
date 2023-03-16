package com.codeforces.iomarkup.types.primitive;

public class Float64 extends FloatPrimitiveType {
    private static final Float64 INSTANCE = new Float64();

    private Float64() {
        super("float64");
    }

    public static Float64 getInstance() {
        return INSTANCE;
    }
}
