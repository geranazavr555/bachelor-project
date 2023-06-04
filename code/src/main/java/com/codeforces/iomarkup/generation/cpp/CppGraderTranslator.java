package com.codeforces.iomarkup.generation.cpp;

import com.codeforces.iomarkup.exec.TargetComponent;
import com.codeforces.iomarkup.generation.FilesTranslator;
import com.codeforces.iomarkup.generation.TranslatedFile;
import com.codeforces.iomarkup.symbol.scope.Scope;

import java.util.List;

public class CppGraderTranslator implements FilesTranslator {
    private static final String TEMPLATE_CPP = """
            #include <iostream>
            #include <vector>

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

            #include "grader.h"
            
            input_t input;
            
            // Functions for input
            %s
                        
            // Functions for output
            %s

            int main(int argc, char *argv[])
            {
                input = read_input();
                write_output(solve(input));
                return 0;
            }
            """;

    private static final String TEMPLATE_H = """
            #include <vector>

            using std::vector;
            
            %s
             
            output_t solve(input_t const& input);
            """;

    private final CppStructDeclarationsTranslator cppStructDeclarationsTranslator;
    private final CppGraderReadTranslator cppGraderReadTranslator;
    private final CppGraderWriteTranslator cppGraderWriteTranslator;

    public CppGraderTranslator(Scope globalScope) {
        cppStructDeclarationsTranslator = new CppStructDeclarationsTranslator(globalScope);
        cppGraderReadTranslator = new CppGraderReadTranslator(globalScope, globalScope.getConstructors());
        cppGraderWriteTranslator = new CppGraderWriteTranslator(globalScope, globalScope.getConstructors());
    }

    @Override
    public List<TranslatedFile> translate() {
        return List.of(
                new TranslatedFile(
                        TargetComponent.GRADER,
                        "grader.cpp",
                        TEMPLATE_CPP.formatted(
                                cppGraderReadTranslator.translate(),
                                cppGraderWriteTranslator.translate()
                        )
                ),
                new TranslatedFile(
                        TargetComponent.GRADER,
                        "grader.h",
                        TEMPLATE_H.formatted(
                                cppStructDeclarationsTranslator.translate()
                        )
                )
        );
    }
}
