package com.codeforces.iomarkup.symbol.resolve;

import java.util.List;

public class NamedStructArrayDescription extends ArrayDescription<VariableDescription> {
    public NamedStructArrayDescription(List<ArrayParameters> arrayParameters, VariableDescription component) {
        super(arrayParameters, component);
    }
}
