package com.codeforces.iomarkup.types.primitive;

public class Float32 extends FloatPrimitiveType {
    private static final Float32 INSTANCE = new Float32();

    private Float32() {
        super("float32");
    }

    public static Float32 getInstance() {
        return INSTANCE;
    }
}
