package com.codeforces.iomarkup;

import com.codeforces.iomarkup.antlr.IoMarkupLexer;
import com.codeforces.iomarkup.antlr.IoMarkupParser;
import com.codeforces.iomarkup.generation.impl.cpp.CppGraderReadTranslator;
import com.codeforces.iomarkup.generation.impl.cpp.CppGraderWriteTranslator;
import com.codeforces.iomarkup.generation.impl.cpp.CppStructDeclarationsTranslator;
import com.codeforces.iomarkup.generation.impl.cpp.CppTestlibValidatorTranslator;
import com.codeforces.iomarkup.symbol.FindGlobalSymbolsVisitor;
import com.codeforces.iomarkup.symbol.resolve.ResolveSymbolsVisitor;
import com.codeforces.iomarkup.symbol.scope.Scope;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
        var lexer = new IoMarkupLexer(CharStreams.fromPath(Path.of(
                "C:\\Programing\\bachelor-thesis\\materials-repo\\examples-2\\1772F.txt"
        )));
        var parser = new IoMarkupParser(new CommonTokenStream(lexer));
        var root = parser.ioMarkup();
        Scope globalScope = new FindGlobalSymbolsVisitor().visitIoMarkup(root);
        globalScope = new ResolveSymbolsVisitor(globalScope).visitIoMarkup(root);

        System.out.println("/////////////////////////////////////// GRADER //");

        System.out.println("""
                #include <bits/stdc++.h>
                
                using namespace std;
                
                """);

        var structTranslator = new CppStructDeclarationsTranslator(globalScope);
        structTranslator.translateToList().forEach(System.out::println);

        var readTranslator = new CppGraderReadTranslator(globalScope, globalScope.getConstructors());
        readTranslator.translateToList().forEach(System.out::println);

        var writeTranslator = new CppGraderWriteTranslator(globalScope, globalScope.getConstructors());
        writeTranslator.translateToList().forEach(System.out::println);

        System.out.println("/////////////////////////////////////// VALIDATOR //");

        System.out.println("""
                #include <bits/stdc++.h>
                #include "testlib.h"
                
                using namespace std;
                
                """);

        structTranslator = new CppStructDeclarationsTranslator(globalScope);
        structTranslator.translateToList().forEach(System.out::println);

        var testlibValidatorTranslator = new CppTestlibValidatorTranslator(globalScope, globalScope.getConstructors());
        testlibValidatorTranslator.translateToList().forEach(System.out::println);

        System.out.println("""
                int main(int argc, char *argv[]) {
                    registerValidation(argc, argv);
                    read_input();
                    inf.readEoln();
                    inf.readEof();
                                
                    return 0;
                }
                """);
        
    }
}
