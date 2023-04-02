package com.codeforces.iomarkup.types;

public interface Type {
    default boolean isAssignableFrom(Type otherType) {
        return this.getClass().isAssignableFrom(otherType.getClass());
    }

    static boolean isAssignable(Class<? extends Type> targetTypeClass, Class<? extends Type> fromTypeClass) {
        return targetTypeClass.isAssignableFrom(fromTypeClass);
    }

    static boolean isAssignable(Class<? extends Type> targetTypeClass, Type fromType) {
        return isAssignable(targetTypeClass, fromType.getClass());
    }
}