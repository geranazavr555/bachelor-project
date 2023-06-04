package com.codeforces.iomarkup.type;

public class StringType extends ArrayType {
    private static final StringType INSTANCE = new StringType();

    public StringType() {
        super(PrimitiveType.CHAR);
        characteristics.add(TypeCharacteristic.LITERAL);
        characteristics.add(TypeCharacteristic.PREDEFINED);
        characteristics.add(TypeCharacteristic.COMPARABLE);
        characteristics.add(TypeCharacteristic.NAMED);
        characteristics.add(TypeCharacteristic.CAN_EQUAL);
    }

    @Override
    public String getName() {
        return "string";
    }

    public static StringType getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean isAssignableFrom(Type other) {
        return other instanceof StringType;
    }
}
