package com.codeforces.iomarkup.symbol.resolve;

import java.util.List;

public class NamedTypeArrayDescription extends ArrayDescription<VariableDescription> {
    public NamedTypeArrayDescription(List<ArrayParameters> arrayParameters, VariableDescription component) {
        super(arrayParameters, component);
    }
}
