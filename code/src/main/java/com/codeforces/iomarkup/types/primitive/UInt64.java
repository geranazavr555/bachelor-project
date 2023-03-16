package com.codeforces.iomarkup.types.primitive;

import java.math.BigInteger;

public class UInt64 extends IntegerPrimitiveType {
    private static final UInt64 INSTANCE = new UInt64();

    public static final BigInteger MIN_VALUE = BigInteger.ZERO;
    public static final BigInteger MAX_VALUE = BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE);

    private UInt64() {
        super("uint64");
    }

    public static UInt64 getInstance() {
        return INSTANCE;
    }
}
