package com.codeforces.iomarkup.symbol;

public class ConstructorArgument extends Symbol {
    private final String type;

    public ConstructorArgument(String name, String type) {
        super(name);
        this.type = type;
    }


    public String getType() {
        return type;
    }
}
