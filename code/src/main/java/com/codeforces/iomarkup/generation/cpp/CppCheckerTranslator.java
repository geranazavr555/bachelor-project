package com.codeforces.iomarkup.generation.cpp;

import com.codeforces.iomarkup.exec.TargetComponent;
import com.codeforces.iomarkup.generation.FilesTranslator;
import com.codeforces.iomarkup.generation.TranslatedFile;
import com.codeforces.iomarkup.symbol.scope.Scope;

import java.util.List;

public class CppCheckerTranslator implements FilesTranslator {
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
            
            input_t input;

            typedef output_t AnsType;

            AnsType readAns(InStream& stream)
            {
                output_t output = read_output(stream);
                
                // Write code for semantic check answer here.
                // For example, here you should check a certificate.
                // You should return a comparable representation of answer's quality.
                
                return output;
            }

            int main(int argc, char *argv[])
            {
                registerTestlibCmd(argc, argv);
                input = read_input(inf);
                AnsType pa_answer = readAns(ouf);
                AnsType jury_answer = readAns(ans);

                // Check here that participant have found answer with the same quality as jury.
                
                return 0;
            }
            """;

    private final CppStructDeclarationsTranslator cppStructDeclarationsTranslator;
    private final CppTestlibCheckerTranslator cppTestlibCheckerTranslator;

    public CppCheckerTranslator(Scope globalScope) {
        cppStructDeclarationsTranslator = new CppStructDeclarationsTranslator(globalScope);
        cppTestlibCheckerTranslator = new CppTestlibCheckerTranslator(globalScope, globalScope.getConstructors());
    }

    @Override
    public List<TranslatedFile> translate() {
        return List.of(
                new TranslatedFile(
                        TargetComponent.CHECKER,
                        "check.cpp",
                        TEMPLATE.formatted(
                                cppStructDeclarationsTranslator.translate(),
                                cppTestlibCheckerTranslator.translate()
                        )
                )
        );
    }
}
