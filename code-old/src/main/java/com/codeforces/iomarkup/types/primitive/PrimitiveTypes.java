package com.codeforces.iomarkup.types.primitive;

public enum PrimitiveTypes {
    BOOL(Bool.getInstance()),
    CHAR(Char.getInstance()),
    FLOAT32(Float32.getInstance()),
    FLOAT64(Float64.getInstance()),
    INT32(Int32.getInstance()),
    INT64(Int64.getInstance()),
    UINT32(UInt32.getInstance()),
    UINT64(UInt64.getInstance());

    private final PrimitiveType typeInstance;

    PrimitiveTypes(PrimitiveType typeInstance) {
        this.typeInstance = typeInstance;
    }

    public PrimitiveType get() {
        return typeInstance;
    }
}
