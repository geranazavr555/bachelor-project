package com.codeforces.iomarkup.generation.java;

import com.codeforces.iomarkup.exec.TargetComponent;
import com.codeforces.iomarkup.generation.FilesTranslator;
import com.codeforces.iomarkup.generation.TranslatedFile;
import com.codeforces.iomarkup.symbol.scope.Scope;

import java.util.List;

public class JavaSolutionTranslator implements FilesTranslator {
    private static final String TEMPLATE = """
            public class Solution {
                // Structure declarations
                %s

                private final java.util.Scanner in;
                private final java.io.PrintStream out;

                public Grader(java.util.Scanner in, java.io.PrintStream out) {
                    this.in = in;
                    this.out = out;
                }

                // Input methods
                %s
                
                // Output methods
                %s

                public Output solve(Input input) {
                    // Write your code here
                }

                public static void main(java.lang.String[] args) {
                    try (java.util.Scanner inputScanner = new java.util.Scanner(java.lang.System.in)) {
                        Solution solution = new Solution(inputScanner, java.lang.System.out);
                        solution.writeOutput(solution.solve(solution.readInput()))
                    }
                }
            }
            """;

    private final JavaStructDeclarationsTranslator javaStructDeclarationsTranslator;
    private final JavaGraderReadTranslator javaGraderReadTranslator;
    private final JavaGraderWriteTranslator javaGraderWriteTranslator;

    public JavaSolutionTranslator(Scope globalScope) {
        javaStructDeclarationsTranslator = new JavaStructDeclarationsTranslator(globalScope);
        javaGraderReadTranslator = new JavaGraderReadTranslator(globalScope, globalScope.getConstructors());
        javaGraderWriteTranslator = new JavaGraderWriteTranslator(globalScope, globalScope.getConstructors());
    }

    @Override
    public List<TranslatedFile> translate() {
        return List.of(
                new TranslatedFile(
                        TargetComponent.VALIDATOR,
                        "Solution.java",
                        TEMPLATE.formatted(
                                javaStructDeclarationsTranslator.translate(),
                                javaGraderReadTranslator.translate(),
                                javaGraderWriteTranslator.translate()
                        )
                )
        );
    }
}
