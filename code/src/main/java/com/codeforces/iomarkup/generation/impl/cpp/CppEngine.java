package com.codeforces.iomarkup.generation.impl.cpp;

import com.codeforces.iomarkup.generation.Engine;
import com.codeforces.iomarkup.symbol.resolve.ConstructorIfAlt;
import com.codeforces.iomarkup.symbol.resolve.ConstructorItem;
import com.codeforces.iomarkup.symbol.resolve.ConstructorWithBody;
import com.codeforces.iomarkup.symbol.resolve.Variable;

import java.util.ArrayList;
import java.util.List;

public class CppEngine implements Engine<CppTargetLanguage> {
    private final CppTargetLanguage cpp = new CppTargetLanguage();

    public List<String> generateStructDefinition(ConstructorWithBody constructor) {
        var result = new ArrayList<String>();
        result.add("struct " + cpp.getTypeName(constructor.getName()));
        result.add("{");

        for (ConstructorItem item : constructor.getBody()) {
            if (item instanceof Variable variable) {

//                result.addAll()

            } else if (item instanceof ConstructorIfAlt ifAlt) {

            }
        }

        result.add("};");
        return result;
    }

}
