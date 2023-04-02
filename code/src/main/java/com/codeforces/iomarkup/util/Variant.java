package com.codeforces.iomarkup.util;

public class Variant<A, B> {
    private final A a;
    private final B b;

    private Variant(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public static <A, B> Variant<A, B> left(A a) {
        return new Variant<>(a, null);
    }

    public static <A, B> Variant<A, B> right(B b) {
        return new Variant<>(null, b);
    }
}
