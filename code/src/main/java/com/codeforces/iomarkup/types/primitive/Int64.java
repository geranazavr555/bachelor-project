package com.codeforces.iomarkup.types.primitive;

public class Int64 extends IntegerPrimitiveType {
    private static final Int64 INSTANCE = new Int64();

    public static final long MIN_VALUE = Long.MIN_VALUE;
    public static final long MAX_VALUE = Long.MAX_VALUE;

    private Int64() {
        super("int64");
    }

    public static Int64 getInstance() {
        return INSTANCE;
    }
}
