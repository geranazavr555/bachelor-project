package com.codeforces.iomarkup.scopes;

import com.codeforces.iomarkup.symbols.IoMarkupItem;
import com.codeforces.iomarkup.symbols.SymbolsTable;
import com.codeforces.iomarkup.symbols.struct.Struct;
import com.codeforces.iomarkup.symbols.struct.VarDeclaration;
import com.codeforces.iomarkup.types.unknown.UnknownType;

import java.util.*;

public class ScopesResolver {
    private static final String INPUT_STRUCT_NAME = "input";
    private static final String OUTPUT_STRUCT_NAME = "output";

    private final SymbolsTable symbolsTable = new SymbolsTable();
    private final Map<String, IoMarkupItem> rawSymbols;
    private final List<Struct> topologicalOrder = new ArrayList<>();
    private final Map<String, Struct> definedStructs = new HashMap<>();

    public ScopesResolver(Map<String, IoMarkupItem> rawSymbols) {
        this.rawSymbols = rawSymbols;
    }

    public void resolve() {
        resolveInput();
        resolveOutput();
    }

    private Struct resolveStruct(String structName) {
        var item = rawSymbols.get(structName);
        if (!(item instanceof Struct struct))
            throw new RuntimeException(); // todo

        symbolsTable.push(struct);
        symbolsTable.pushScope(struct);
        definedStructs.put(structName, struct);

        int toPop = 0;
        for (VarDeclaration varDeclaration : struct.getVarDeclarations()) {
            var variable = varDeclaration.getVariable();
            if (variable.getType().orElseThrow(RuntimeException::new) instanceof UnknownType unknownType) {
                var name = unknownType.getName();
//                definedStructs.compute(name, (key, oldValue, newValue) -> {
////                    if (oldValue == null)
//                });
                var innerStruct = resolveStruct(name);
                symbolsTable.push(innerStruct);
                toPop++;
            }

            varDeclaration.resolveWithScope(symbolsTable);
            symbolsTable.push(variable);
            toPop++;

            // todo: iter
        }

        symbolsTable.pop(toPop);
        symbolsTable.popScope();
        return struct;
    }

    private void resolveInput() {
        resolveStruct(INPUT_STRUCT_NAME);
    }

    private void resolveOutput() {
        // todo
    }
}
