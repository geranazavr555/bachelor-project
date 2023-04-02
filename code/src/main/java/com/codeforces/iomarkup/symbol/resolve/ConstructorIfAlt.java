package com.codeforces.iomarkup.symbol.resolve;

import com.codeforces.iomarkup.pl.PlExpression;
import lombok.AllArgsConstructor;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class ConstructorIfAlt implements ConstructorItem {
    private final PlExpression conditionalExpression;
    private final List<ConstructorItem> trueItems;
    private final List<ConstructorItem> falseItems;

    public List<ConstructorItem> getTrueItems() {
        return Collections.unmodifiableList(trueItems);
    }

    public List<ConstructorItem> getFalseItems() {
        return Collections.unmodifiableList(falseItems);
    }
}
