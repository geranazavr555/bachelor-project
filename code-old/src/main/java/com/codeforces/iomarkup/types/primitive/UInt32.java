package com.codeforces.iomarkup.types.primitive;

public class UInt32 extends IntegerPrimitiveType {
    private static final UInt32 INSTANCE = new UInt32();

    public static final long MIN_VALUE = 0;
    public static final long MAX_VALUE = (1L << 32) - 1;

    private UInt32() {
        super("uint32");
    }

    public static UInt32 getInstance() {
        return INSTANCE;
    }
}
