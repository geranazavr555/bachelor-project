package com.codeforces.iomarkup.types.primitive;

public class Int32 extends IntegerPrimitiveType {
    private static final Int32 INSTANCE = new Int32();

    public static final int MIN_VALUE = Integer.MIN_VALUE;
    public static final int MAX_VALUE = Integer.MAX_VALUE;


    private Int32() {
        super("int32");
    }

    public static Int32 getInstance() {
        return INSTANCE;
    }
}
