package com.codeforces.iomarkup.pl;

public class PlSubscript extends PlExpression {
    private final PlExpression array;
    private final PlExpression index;

    public PlSubscript(PlExpression array, PlExpression index) {
        this.array = array;
        this.index = index;
    }
}
