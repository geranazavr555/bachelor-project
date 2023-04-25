package com.codeforces.iomarkup.generation.cpp;

import com.codeforces.iomarkup.exec.TargetComponent;
import com.codeforces.iomarkup.generation.FilesTranslator;
import com.codeforces.iomarkup.generation.TranslatedFile;
import com.codeforces.iomarkup.symbol.scope.Scope;

import java.util.List;

public class CppSolutionTranslator implements FilesTranslator {
    private static final String TEMPLATE = """
            #include <iostream>
            #include <vector>

            using std::cin;
            using std::cout;
            using std::vector;

            // Structure declarations
            %s
                        
            // Functions for input
            %s
                        
            // Functions for output
            %s

            output_t solve(input_t const& input)
            {
                // Write your code here.
            }

            int main(int argc, char *argv[])
            {
                write_output(solve(read_input()));
                return 0;
            }
            """;

    private final CppStructDeclarationsTranslator cppStructDeclarationsTranslator;
    private final CppGraderReadTranslator cppGraderReadTranslator;
    private final CppGraderWriteTranslator cppGraderWriteTranslator;

    public CppSolutionTranslator(Scope globalScope) {
        cppStructDeclarationsTranslator = new CppStructDeclarationsTranslator(globalScope);
        cppGraderReadTranslator = new CppGraderReadTranslator(globalScope, globalScope.getConstructors());
        cppGraderWriteTranslator = new CppGraderWriteTranslator(globalScope, globalScope.getConstructors());
    }

    @Override
    public List<TranslatedFile> translate() {
        return List.of(
                new TranslatedFile(
                        TargetComponent.SOLUTION,
                        "solution.cpp",
                        TEMPLATE.formatted(
                                cppStructDeclarationsTranslator.translate(),
                                cppGraderReadTranslator.translate(),
                                cppGraderWriteTranslator.translate()
                        )
                )
        );
    }
}
