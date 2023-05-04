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

            namespace iomarkup
            {
                int pow(int a, int b)
                {
                    if (b == 0) return 1;
                    if (b %% 2 == 1) return a * pow(a, b - 1);
                    return pow(a * a, b / 2);
                }
                
                long long pow(long long a, long long b)
                {
                    if (b == 0) return 1ll;
                    if (b %% 2 == 1) return a * pow(a, b - 1ll);
                    return pow(a * a, b / 2ll);
                }
            }

            using std::cin;
            using std::cout;
            using std::vector;

            // Structure declarations
            %s
            
            input_t input;

            // Functions for input
            %s
                        
            int main(int argc, char *argv[])
            {
                registerValidation(argc, argv);
                input = read_input();
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
