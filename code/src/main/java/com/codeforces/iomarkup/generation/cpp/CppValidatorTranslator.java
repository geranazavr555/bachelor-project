package com.codeforces.iomarkup.generation.cpp;

import com.codeforces.iomarkup.exec.TargetComponent;
import com.codeforces.iomarkup.generation.FilesTranslator;
import com.codeforces.iomarkup.generation.TranslatedFile;
import com.codeforces.iomarkup.symbol.scope.Scope;

import java.util.List;

public class CppValidatorTranslator implements FilesTranslator {
    private static final String TEMPLATE = """
            #include <iostream>
            #include <vector>
            #include "testlib.h"

            using std::cin;
            using std::cout;
            using std::vector;

            // Structure declarations
            %s
                        
            // Functions for input
            %s
                        
            int main(int argc, char *argv[])
            {
                registerValidation(argc, argv);
                input_t input = read_input();
                inf.readEoln();
                inf.readEof();
                
                // Write code for semantic input validation here.

                return 0;
            }
            """;

    private final CppStructDeclarationsTranslator cppStructDeclarationsTranslator;
    private final CppTestlibValidatorTranslator cppTestlibValidatorTranslator;

    public CppValidatorTranslator(Scope globalScope) {
        cppStructDeclarationsTranslator = new CppStructDeclarationsTranslator(globalScope);
        cppTestlibValidatorTranslator = new CppTestlibValidatorTranslator(globalScope, globalScope.getConstructors());
    }

    @Override
    public List<TranslatedFile> translate() {
        return List.of(
                new TranslatedFile(
                        TargetComponent.VALIDATOR,
                        "v.cpp",
                        TEMPLATE.formatted(
                                cppStructDeclarationsTranslator.translate(),
                                cppTestlibValidatorTranslator.translate()
                        )
                )
        );
    }
}
