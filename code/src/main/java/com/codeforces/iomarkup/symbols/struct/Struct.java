package com.codeforces.iomarkup.symbols.struct;

import com.codeforces.iomarkup.symbols.IoMarkupItem;
import com.codeforces.iomarkup.types.Type;

import java.util.List;

public class Struct extends IoMarkupItem implements Type {
    private final List<StructItem> items;

    public Struct(String name, List<StructItem> items) {
        super(name);
        this.items = items;
    }

    public List<VarDeclaration> getVarDeclarations() {
        return items.stream()
                .filter(item -> item instanceof VarDeclaration)
                .map(item -> (VarDeclaration)item)
                .toList();
    }
}
